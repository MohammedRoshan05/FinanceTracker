package main

import (
	"database/sql"
	"fmt"
	"time"

	_ "github.com/lib/pq"
	"github.com/pelletier/go-toml/query"
	"golang.org/x/crypto/bcrypt"
)

type PostgresDB struct{
	db *sql.DB
}

type Storage interface {
	CreateAccount(*Account) error
	DeleteAccount(string) error
	Login(*LoginAccReq) (error)
	GetAccountByEmail(string) (*Account,error)
	GetAccounts() ([]* Account,error)

	CreateTransaction(*Transactions) (error)
	GetTransactions(*Account) ([]* Transactions,error) 
	UpdateBalance(*Account,int) (error)
}


func ConnectDB()(*PostgresDB,error){
	connstr := "user=postgres dbname=postgres password=finance sslmode=disable"
	db,err := sql.Open("postgres",connstr)
	if err != nil {
		return nil, err
	}
	if err := db.Ping(); err != nil {
		return nil, err
	}
	return &PostgresDB{
		db: db,
		},nil
	}

func (s *PostgresDB) createAccountTable() error {
	query := `create table if not exists Account(
		id serial primary key,
		User_ID int unique not null,
		Email varchar(250),
		Password varchar(1550),
		Balance float not null
	)`
	_,err := s.db.Exec(query)
	return err
}

func (s *PostgresDB) createTransactionsTable() error {
	query := `
	CREATE TABLE IF NOT EXISTS Transactions (
		id SERIAL PRIMARY KEY,
		User_ID INT NOT NULL,
		amount INT NOT NULL,
		date DATE NOT NULL,
		type TEXT NOT NULL,
		FOREIGN KEY (User_ID) REFERENCES Account(User_ID)
		)
		`
		_, err := s.db.Exec(query)
		return err
}

func (s *PostgresDB) CreateAccount(acc *Account) error {
	query := `insert into Account 
	(USER_ID,Email,Password,Balance)
	values ($1,$2,$3,$4)
	`
	_,err := s.db.Exec(query,
		acc.User_ID,
		acc.Password,
		acc.Password,
		0,
	)
	return err
}

func (s *PostgresDB) DeleteAccount(Email string) error {
	query := `delete from account where Email = $1`
	_,err := s.db.Query(query,Email)
	return err
}

func (s *PostgresDB) Login(req *LoginAccReq) (error) {
	row := s.db.QueryRow(
		`SELECT password
		   FROM Account
		  WHERE email = $1`,
		req.Email,
	)
	dbPassword := ""
	if err := row.Scan(&dbPassword); err != nil {
		return err
	}
	
	if err := bcrypt.CompareHashAndPassword([]byte(dbPassword),[]byte(req.Password)); err != nil {
		fmt.Println("Raw password:", req.Password)
		fmt.Println("Hashed password from DB:", dbPassword)
		return err
	}
	return nil
}


func (s *PostgresDB) CreateTransaction(trans *Transactions) (error) {
	query := `insert into Transactions
		(User_ID,amount,date,type)
		values($1,$2,$3,$4)
	`
	_,err := s.db.Exec(query,
		trans.User_ID,
		trans.Amount,
		trans.Date,
		trans.Type,
	)
	return err
}


func (s *PostgresDB) GetTransactions(acc *Account) ([]* Transactions,error) {
	query := `select * from Transactions where USER_ID = $1`
	rows,err := s.db.Query(query,acc.User_ID)
	if(err != nil){
		return nil,err
	}

	transactions := []*Transactions{}
	for rows.Next(){
		transaction := new(Transactions)
		err := rows.Scan(
			&transaction.ID,
			&transaction.User_ID,
			&transaction.Amount,
			&transaction.Date,
			&transaction.Type,
		)
		if(err != nil){
			return nil,err
		}
		transactions = append(transactions, transaction)
	}
	return transactions,nil
}

func (s *PostgresDB) UpdateBalance(acc *Account,due int) (error) {
	query := `update Account 
		set balance = balance + $2
		where Email = $1
	`
	_,err := s.db.Exec(query,acc.Email,due)
	return err
}



func (s *PostgresDB) GetAccountByEmail(email string) (*Account,error) {
	query := `select USER_ID, Email, Balance from account where Email = $1`
	row := s.db.QueryRow(query,email)

	account := new(Account)
	err := row.Scan(
		&account.User_ID,
		&account.Password,
		&account.Balance,
	)
	if(err != nil){
		return nil,err
	}
	return account,nil
}
// func (s *PostgresDB) (){}






