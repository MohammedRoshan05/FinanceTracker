package main

import (
	"log"
	"time"
)

func seedDB(s *APIServer){
	userID := "b63c5cf4-e318-4ba7-939b-0bd5ad18948d"
 	start := time.Now().AddDate(0,0,0)

  for i := 0; i < 10; i++ {
    d := start.AddDate(0, 0, 0)
    tx := &Transactions{
      User_ID: userID,
      Amount:  100,          
      Date:    CustomDate{Time: d},
      Type:    "credit",
    }
    if err := s.Store.CreateTransaction(tx); err != nil {
      log.Fatalf("failed on day %d (%v): %v", i, d, err)
    }
    log.Printf("inserted day %d: %s", i, d.Format("2006-01-02"))
  }
}