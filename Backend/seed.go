package main

import (
	"log"
	"time"
)

func seedDB(s *APIServer){
	userID := "11ed95f9-fc11-45d8-aacc-adc77d38c9f5"
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