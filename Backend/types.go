package main

import "time"

type Account struct{
	ID				int				`json:"id"`
	Email 			string			`json:"email"`
	Password		string			`json:"-"`
	User_ID			string			`json:"user_id"`
	Balance			float32			`json:"balance"`
}

type CreateAccReq struct{
	Email 			string		`json:"email"`
	Password		string		`json:"password"`
}

type LoginAccReq struct {
	Email 			string		`json:"email"`
	Password		string		`json:"password"`
}

type Transactions struct{
	ID				int			`json:"id"`
	User_ID			string		`json:"user_id"`
	Amount 			int			`json:"amount"`
	Date			time.Time	`json:"date"`
	Type			string		`json:"type"`
}

type updateTransaction struct {
	Email			string		`json:"user_id"`
	Amount 			int			`json:"amount"`
	Date			time.Time	`json:"date"`
	Type			string		`json:"type"`
}