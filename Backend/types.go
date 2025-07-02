package main

import (
	"strings"
	"time"
)

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
	Date			CustomDate	`json:"date"`
	Type			string		`json:"type"`
}

type updateTransaction struct {
	Email			string		`json:"user_id"`
	Amount 			int			`json:"amount"`
	Date			CustomDate	`json:"date"`
	Type			string		`json:"type"`
}

type CustomDate struct {
	Time time.Time
}

// Custom unmarshal to parse "YYYY/MM/DD"
func (cd *CustomDate) UnmarshalJSON(b []byte) error {
	s := strings.Trim(string(b), `"`)
	t, err := time.Parse("2006/01/02", s)
	if err != nil {
		return err
	}
	cd.Time = t
	return nil
}

func (cd CustomDate) MarshalJSON() ([]byte, error) {
    s := cd.Time.Format("2006/01/02")
    return []byte(`"` + s + `"`), nil
}