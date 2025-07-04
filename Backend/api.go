package main

import (
	"context"
	"encoding/json"
	"fmt"

	// "fmt"
	"log"
	"net/http"
	"time"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/cors"
	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
	"golang.org/x/crypto/bcrypt"
)

type APIServer struct {
	ListenAddr string
	Store      Storage
}

type APIError struct {
	Error string `json:"error"`
}

type APIFunc func(http.ResponseWriter, *http.Request) error

func createNewServer(listenAddr string, store Storage) *APIServer {
	return &APIServer{
		ListenAddr: listenAddr,
		Store:      store,
	}
}

func (s *APIServer) Run() {
	r := chi.NewRouter()
	r.Use(middleware.RequestID)
	r.Use(middleware.Logger)
	r.Use(middleware.Recoverer)

	// CORS middleware
	r.Use(cors.Handler(cors.Options{
		AllowedOrigins:   []string{
			"http://localhost:5173",
			"http://10.0.2.2:8000",    
		},
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowedHeaders:   []string{"Accept", "Authorization", "Content-Type", "X-Custom-Header"},
		ExposedHeaders:   []string{"Authorization"},
		AllowCredentials: true,
		MaxAge:           300,
	}))

	log.Println("JSON API running on port ", s.ListenAddr)
	r.Post("/create", makeHTTPHandlerFunc(s.handleCreateAccount))
	r.Post("/login", makeHTTPHandlerFunc(s.handleLogin))
	r.Post("/delete/{id}", withJWTAuth(makeHTTPHandlerFunc(s.handleDeleteAccount), s.Store))

	r.Post("/transaction/{id}", withJWTAuth(makeHTTPHandlerFunc(s.handleNewTransaction), s.Store))
	r.Get("/transaction/{id}", withJWTAuth(makeHTTPHandlerFunc(s.handleGetTransactions), s.Store))
	// r.Post("/transaction/{id}", withJWTAuth(makeHTTPHandlerFunc(s.handleDeleteTransaction), s.Store))

	r.Get("/balance/{id}",withJWTAuth(makeHTTPHandlerFunc(s.handleGetDailyBalance),s.Store))
	r.Get("/transactions/{id}",withJWTAuth(makeHTTPHandlerFunc(s.handleGetDailyTransactions),s.Store))

	r.Post("/{id}/balance", withJWTAuth(makeHTTPHandlerFunc(s.handleBalanceUpdate), s.Store))

	http.ListenAndServe(":8000", r)
}

func (s *APIServer) handleCreateAccount(w http.ResponseWriter, r *http.Request) error {
	createAccReq := new(CreateAccReq)
	if err := json.NewDecoder(r.Body).Decode(createAccReq); err != nil {
		fmt.Println(err)
		fmt.Println(1)
		return err
	}
	account, err := NewAccount(createAccReq.Email, createAccReq.Password)
	if err != nil {
		return err
	}
	fmt.Println(account)
	if err := s.Store.CreateAccount(account); err != nil {
		return err
	}
	return WriteJSON(w, http.StatusOK, account)
}

func (s *APIServer) handleDeleteAccount(w http.ResponseWriter, r *http.Request) error {
	account := r.Context().Value("account").(*Account)
	if err := s.Store.DeleteAccount(account); err != nil {
		return err
	}
	return nil
}

func (s *APIServer) handleBalanceUpdate(w http.ResponseWriter, r *http.Request) error {
	due := 0
	if err := json.NewDecoder(r.Body).Decode(&due); err != nil {
		return err
	}
	account := r.Context().Value("account").(*Account)
	if err := s.Store.UpdateBalance(account, due); err != nil {
		fmt.Println(err)
		return err
	}
	return nil
}

func (s *APIServer) handleNewTransaction(w http.ResponseWriter, r *http.Request) error {
	newTransReq := new(updateTransaction)
	if err := json.NewDecoder(r.Body).Decode(newTransReq); err != nil {
		return err
	}
	account := r.Context().Value("account").(*Account)

	transaction := Transactions{
		User_ID: account.User_ID,
		Amount:  newTransReq.Amount,
		Date:    newTransReq.Date,
		Type:    newTransReq.Type,
	}
	fmt.Println("Created Transaction")
	if err := s.Store.CreateTransaction(&transaction); err != nil {
		fmt.Println(err)
		return err
	}
	return nil
}

func (s *APIServer) handleDeleteTransaction(w http.ResponseWriter, r *http.Request) error {
	delTrans := new(updateTransaction)
	if err := json.NewDecoder(r.Body).Decode(delTrans); err != nil {
		return err
	}
	account := r.Context().Value("account").(*Account)
	if err := s.Store.UpdateBalance(account, 0 - delTrans.Amount); err != nil {
		return err
	}
	err := s.Store.DeleteTransaction(delTrans,account.User_ID)
	return err
}

func (s *APIServer) handleGetTransactions(w http.ResponseWriter, r *http.Request) error {
	account := r.Context().Value("account").(*Account)
	transactions, err := s.Store.GetTransactions(account)
	if err != nil {
		fmt.Println(err)
		return err
	}
	return WriteJSON(w, http.StatusOK, transactions)
}

func (s *APIServer) handleGetDailyTransactions(w http.ResponseWriter, r *http.Request) error {
	account := r.Context().Value("account").(*Account)
	dailyTransactions,err := s.Store.GetDailyTransactions(account)
	if(err != nil){
		return err
	}
	return WriteJSON(w,http.StatusOK,dailyTransactions)

}

func (s *APIServer) handleGetDailyBalance(w http.ResponseWriter, r *http.Request) error {
	account := r.Context().Value("account").(*Account)
	fmt.Println("entering API")
	dailyBalance,err := s.Store.getDailyBalance(account)
	if(err != nil){
		return err
	}
	return WriteJSON(w,http.StatusOK,dailyBalance)
}

func (s *APIServer) handleLogin(w http.ResponseWriter, r *http.Request) error {
	loginreq := new(LoginAccReq)
	if err := json.NewDecoder(r.Body).Decode(loginreq); err != nil {
		return err
	}
	println("Login Req")
	println(loginreq.Email + loginreq.Password)
	if err := s.Store.Login(loginreq); err != nil {
		return err
	}
	tokenString, err := createJWT(loginreq)
	if err != nil {
		return err
	}
	w.Header().Set("Authorization", tokenString)
	return WriteJSON(w, http.StatusOK, tokenString)
}

// func (s *APIServer) handleGetAccount(w http.ResponseWriter,r *http.Request) error {
// 	account := r.Context().Value("account").(*Account)
// 	return WriteJSON(w, http.StatusOK, account)
// }

func makeHTTPHandlerFunc(f APIFunc) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		if err := f(w, r); err != nil {
			//error response
			fmt.Println(err)
			WriteJSON(w, http.StatusBadRequest, APIError{Error: "Invalid Login Credentials"})
		}
	}
}

func createJWT(loginreq *LoginAccReq) (string, error) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"issued_time": time.Now().Unix(),
		"email":       loginreq.Email,
	})
	return token.SignedString([]byte("Test"))
}

func validateJWT(JWT string) (*jwt.Token, error) {
	token, err := jwt.Parse(JWT, func(t *jwt.Token) (interface{}, error) {
		return []byte("Test"), nil
	}, jwt.WithValidMethods([]string{jwt.SigningMethodHS256.Alg()}))
	if err != nil {
		return token, err
	}
	return token, nil
}

func withJWTAuth(handlerFunc http.HandlerFunc, s Storage) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		fmt.Println("Executing JWT middleware")
		tokenString := r.Header.Get("Authorization")
		token, err := validateJWT(tokenString)
		if err != nil || !token.Valid {
			permissionDenied(w)
			fmt.Println("JWT is invalid")
			return
		}

		emailID := chi.URLParam(r, "id")
		fmt.Println("fetching" + emailID)
		account, err := s.GetAccountByEmail(emailID)
		if err != nil {
			WriteJSON(w, http.StatusBadRequest, APIError{Error: "No user exists in the db with that id"})
			return
		}
		fmt.Println("after" + emailID)
		fmt.Println(account)

		claims := token.Claims.(jwt.MapClaims)
		fmt.Println(claims)
		if account.Email != string(claims["email"].(string)) {
			fmt.Println("body->" + account.Email)
			fmt.Println("claims ->" + string(claims["email"].(string)))
			fmt.Println("Wrong user trying to access data")
		}
		ctx := context.WithValue(r.Context(), "account", account)
		r = r.WithContext(ctx)
		fmt.Println()
		fmt.Println("JWT Passed")
		handlerFunc(w, r)
	}
}

func permissionDenied(w http.ResponseWriter) {
	WriteJSON(w, http.StatusForbidden, APIError{Error: "Access Denied"})
}

func WriteJSON(w http.ResponseWriter, status int, v any) error {
	w.Header().Add("Content-Type", "application/json")
	w.WriteHeader(status)

	return json.NewEncoder(w).Encode(v)
}

func NewAccount(Email, Password string) (*Account, error) {
	encPW, err := bcrypt.GenerateFromPassword([]byte(Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, err
	}
	return &Account{
		Email:    Email,
		Password: string(encPW),
		User_ID:  string(uuid.New().String()),
	}, nil
}
