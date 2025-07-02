package main

import "fmt"


func main(){
	store, err := ConnectDB()
	if(err != nil){
		fmt.Println(err)
		fmt.Println("1")
	}
	if err := store.Init(); err != nil {
		fmt.Println(err)
		fmt.Println("2")

	}
	server := APIServer{
		ListenAddr: "8000",
		Store: store,
	}	
	seedDB(&server)
	server.Run()
}