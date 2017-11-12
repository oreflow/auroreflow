package main

import (
	"auroreflow/go/lightbulbs"
	"time"
	"net/http"
	"fmt"
	"encoding/json"
	"io/ioutil"
	"log"
)

const BROADCAST_INTERVAL = 10 * time.Minute


func broadcastForLighbulbsThread() {
	for {
		lightbulbs.BroadcastForLightbulbs()
		time.Sleep(BROADCAST_INTERVAL)
	}
}
func handleUpdateCtRequest(w http.ResponseWriter, r *http.Request) {
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
		return
	}
	var request lightbulbs.CtRequest
	json.Unmarshal(body, &request)
	lightbulbs.UpdateCt(request)
}

func handleUpdateHsvRequest(w http.ResponseWriter, r *http.Request) {
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
		return
	}
	var request lightbulbs.HsvRequest
	json.Unmarshal(body, &request)
	lightbulbs.UpdateHsv(request)
}

func handleUpdatePowerRequest(w http.ResponseWriter, r *http.Request) {
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Fatal(err)
		return
	}
	var request lightbulbs.PowerRequest
	json.Unmarshal(body, &request)
	lightbulbs.UpdatePower(request)
}
func handleUpdateNameRequest(w http.ResponseWriter, r *http.Request) {
}

func handleListRequest(w http.ResponseWriter, r *http.Request) {
	fmt.Println(r)
	bulbs := lightbulbs.GetLightbulbs()
	serialized, _ := json.Marshal(bulbs)
	w.Header().Set("Content-Type", "application/json")
	w.Write(serialized)
}

func handlePoweroffRequest(w http.ResponseWriter, r *http.Request) {
	lightbulbs.PowerOffAll()
}

func main() {
	go lightbulbs.UdpListener()
	go broadcastForLighbulbsThread()

	fs := http.FileServer(http.Dir("../web/public/"))
	http.Handle("/", fs)
	http.HandleFunc("/lightbulb/update/ct", handleUpdateCtRequest)
	http.HandleFunc("/lightbulb/update/hsv", handleUpdateHsvRequest)
	http.HandleFunc("/lightbulb/update/power", handleUpdatePowerRequest)
	http.HandleFunc("/lightbulb/update/name", handleUpdateNameRequest)
	http.HandleFunc("/lightbulb/list", handleListRequest)
	http.HandleFunc("/poweroff", handlePoweroffRequest)
	http.ListenAndServe(":8080", nil)
}
