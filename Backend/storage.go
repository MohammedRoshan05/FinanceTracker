package main

import (
	"database/sql"
	"fmt"
	"time"

	_ "github.com/lib/pq"
	"golang.org/x/crypto/bcrypt"
)

type PostgresDB struct{
	db *sql.DB
}

type Storage interface {
	CreateAccount(*Account) error
	DeleteAccount(*Account) error
	Login(*LoginAccReq) (error)
	GetAccountByEmail(string) (*Account,error)
	GetAccounts() ([]* Account,error)

	CreateTransaction(*Transactions) (error)
	DeleteTransaction(trans *updateTransaction,USER_ID string) (error)
	GetTransactions(*Account) ([]* Transactions,error)
	getDailyBalance(*Account) ([] *DailyBalance,error)

	GetDailyTransactions(*Account) ([]* DailyTransactions,error) 
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

func (s *PostgresDB) Init() error {
	if err := s.createAccountTable(); err != nil {
		return err
	}
	if err := s.createTransactionsTable(); err != nil {
		return err
	}
	if err := s.createDayBalanceTable(); err != nil {
		return err
	}
	if err := s.createDayTransactionsTable(); err != nil {
		return err
	}
	return nil
}

func (s *PostgresDB) createAccountTable() error {
	query := `create table if not exists Account(
		id serial primary key,
		User_ID varchar(250) unique not null,
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
		User_ID varchar(250) NOT NULL,
		amount FLOAT NOT NULL,
		date DATE NOT NULL,
		type TEXT NOT NULL,
		FOREIGN KEY (User_ID) REFERENCES Account(User_ID)
		)
		`
	_, err := s.db.Exec(query)
	return err
}

func (s *PostgresDB) createDayBalanceTable() error {
	query := `

	CREATE TABLE IF NOT EXISTS DayBalance (
		id SERIAL PRIMARY KEY,
		User_ID varchar(250) NOT NULL,
		balance FLOAT NOT NULL,
		date DATE NOT NULL,

		CONSTRAINT daybalance_user_date_unique UNIQUE (User_ID, date),
		FOREIGN KEY (User_ID) REFERENCES Account(User_ID)
		)
		`
	_, err := s.db.Exec(query)
	return err
}

func (s *PostgresDB) createDayTransactionsTable() error {
	query := `

	CREATE TABLE IF NOT EXISTS DayTransactions (
		id SERIAL PRIMARY KEY,
		User_ID varchar(250) NOT NULL,
		total FLOAT NOT NULL,
		date DATE NOT NULL,

		CONSTRAINT daytransactions_user_date_unique UNIQUE (User_ID, date),
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
	fmt.Println("query string formed")
	_,err := s.db.Exec(query,
		acc.User_ID,
		acc.Email,
		acc.Password,
		0,
	)
	fmt.Println(err )
	fmt.Println("no error")
	return err
}

func (s *PostgresDB) GetAccounts() ([]* Account,error) {
	return nil,nil
}

func (s *PostgresDB) DeleteAccount(acc *Account) error {
	query := `delete from Transactions where USER_ID = $1`
	_,err := s.db.Query(query,acc.User_ID)
	if(err != nil) {
		return err
	}

	query = `delete from Account where Email = $1`
	_,err = s.db.Query(query,acc.Email)
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
		trans.Date.Time,
		trans.Type,
	)
	if(err != nil) {
		return err
	}
	query = `
		INSERT INTO DayTransactions (user_id, date, total)
		VALUES ($1, $2::date, $3)
		ON CONFLICT (user_id, date)
		DO UPDATE
			SET total = DayTransactions.total + EXCLUDED.total;
		`
	_,err = s.db.Exec(query,
		trans.User_ID,
		trans.Date.Time,
		trans.Amount,
	)
	if(err != nil) {
		return err
	}
	query = `insert into DayBalance (user_id,date,balance)
			values($1, $2::date, $3)
			on conflict (user_id,date)
			do update 
			set Balance = EXCLUDED.Balance + DayBalance.Balance
	`
	_,err = s.db.Exec(query,
		trans.User_ID,
		trans.Date.Time,
		trans.Amount,
	)
	if(err != nil) {
		fmt.Println(3)
		return err
	}
	err = s.rollingDayUpdates(trans.User_ID,trans.Date)
	return err
}

func (s *PostgresDB) DeleteTransaction(trans *updateTransaction,USER_ID string) (error) {
	query := `delete from Transactions where 
	USER_ID = $1 AND
	Amount = $2 AND
	Date = $3 AND
	Type = $4
	`
	_,err := s.db.Query(query,
		USER_ID,
		trans.Amount,
		trans.Date.Time,
		trans.Type,
	)
	if(err != nil){
		return err
	}

	query = `update DayTransactions 
		set total = total - $3
		where USER_ID = $1 AND
		Day = $2
	`
	_,err = s.db.Exec(query,
		USER_ID,
		trans.Date,
		trans.Amount,
	)

	query = `update DayBalance 
		set Balance = Balance - $3
		where USER_ID = $1 AND
		Day = $2
	`
		_,err = s.db.Exec(query,
		USER_ID,
		trans.Date,
		trans.Amount,
	)
	err = s.rollingDayUpdates(USER_ID,trans.Date)

	return nil
}

func (s *PostgresDB) rollingDayUpdates(User_ID string, startDate CustomDate) error {
	fmt.Println("entering rolling")
	startDate.Time = startDate.Time.Truncate(24 * time.Hour)
	var latestDate CustomDate
	err := s.db.QueryRow(`SELECT MAX(date) FROM DayBalance WHERE user_id = $1`, User_ID).Scan(&latestDate.Time)
	if err != nil {
		fmt.Println(1)
		return err
	}

	if startDate.Time.Equal(latestDate.Time){
		var prevBal int64
		var todayTrans int64
		if err = s.db.QueryRow(`
			SELECT balance FROM DayBalance WHERE user_id = $1 AND date = $2`, User_ID, startDate.Time.AddDate(0, 0, -1)).Scan(&prevBal); err != nil {
				fmt.Println("First Date Entry")
				return nil
				// return err
			}
		if err = s.db.QueryRow(`
			SELECT total FROM DayTransactions WHERE user_id = $1 AND date = $2`, User_ID, startDate.Time).Scan(&todayTrans); err != nil {
				return err
			}
		
		_, err = s.db.Exec(`
			UPDATE DayBalance
			SET balance = $3
			WHERE user_id = $1 AND date = $2
		`, User_ID, startDate.Time, prevBal+todayTrans)
		
		if err != nil {
			return err
		}
			
		fmt.Println("Not first date entry")
		return nil
	}

	for d := startDate.Time.AddDate(0,0,1); !d.After(latestDate.Time); d = d.AddDate(0, 0, 1) {
		var prevBal int64
		var netChange int64

		err = s.db.QueryRow(`
			SELECT balance FROM DayBalance WHERE user_id = $1 AND date = $2`, User_ID, d.AddDate(0, 0, -1)).Scan(&prevBal)
		if err != nil {
			fmt.Println(1)
			fmt.Println(err)
			return err
		}

		err = s.db.QueryRow(`
			SELECT total FROM DayTransactions WHERE user_id = $1 AND date = $2`, User_ID, d).Scan(&netChange)
		if err != nil {
			fmt.Println(err)
			return err 
		}
		fmt.Println(string(netChange) +"<- ->"+ string(prevBal))
		_, err = s.db.Exec(`
			UPDATE DayBalance
			SET balance = $3
			WHERE user_id = $1 AND date = $2
		`, User_ID, d, prevBal+netChange)
		
		if err != nil {
			fmt.Println(err)
			return err

		}
	}
	return nil
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
			&transaction.Date.Time,
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
		&account.Email,
		&account.Balance,
	)
	if(err != nil){
		fmt.Println(err)
		return nil,err
	}

	return account,nil
}
// func (s *PostgresDB) (){}

func (s *PostgresDB) GetDailyTransactions(acc *Account) ([]* DailyTransactions,error) {
	query := `select date,total from daytransactions where user_id = $1`
	rows,err := s.db.Query(query,acc.User_ID)
	if(err != nil){
		return nil,err
	}
	dailyTrans := []*DailyTransactions{}
	for(rows.Next()){
		todayTrans := new(DailyTransactions)
		err := rows.Scan(
			&todayTrans.Date.Time,
			&todayTrans.Total,
		)
		if(err != nil){
			return nil,err
		}
		dailyTrans = append(dailyTrans,todayTrans)
	}
	return dailyTrans,nil
}

func (s *PostgresDB) getDailyBalance(acc *Account) ([] *DailyBalance,error){
	fmt.Println("Entering sql func")
	query := `select date,balance from daybalance where user_id = $1`
	rows,err := s.db.Query(query,acc.User_ID)
	if(err != nil){
		fmt.Println(err)
		return nil,err
	}
	dailyBal := []*DailyBalance{}
	for(rows.Next()){
		todayBal := new(DailyBalance)
		err := rows.Scan(
			&todayBal.Date.Time,
			&todayBal.Balance,
		)
		if(err != nil){
			fmt.Println(err)
			return nil,err
		}
		dailyBal = append(dailyBal,todayBal)
	}
	return dailyBal,nil
}






