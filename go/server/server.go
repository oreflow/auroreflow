package main

import (
	"auroreflow/go/lightbulbs"
	"time"
	"net/http"
	"fmt"
	"encoding/json"
	"io/ioutil"
	"log"
	"github.com/gorilla/websocket"
	"sync"
)

const BROADCAST_INTERVAL = 10 * time.Minute


func broadcastForLighbulbsThread() {
	for {
		lightbulbs.BroadcastForLightbulbs()
		time.Sleep(BROADCAST_INTERVAL)
	}
}
func writeEmptyResponse(w http.ResponseWriter) {
	_, err := w.Write([]byte("{}"))
	if err != nil {
		log.Println(err)
	}
}

func handleUpdateCtRequest(w http.ResponseWriter, r *http.Request) {
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Println(err)
		return
	}
	var request lightbulbs.CtRequest
	json.Unmarshal(body, &request)
	lightbulb := lightbulbs.UpdateCt(request)
	writeEmptyResponse(w)
	go broadCastToSockets(lightbulb)
}

func handleUpdateHsvRequest(w http.ResponseWriter, r *http.Request) {
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Println(err)
		return
	}
	var request lightbulbs.HsvRequest
	json.Unmarshal(body, &request)
	lightbulb := lightbulbs.UpdateHsv(request)
	writeEmptyResponse(w)
	go broadCastToSockets(lightbulb)
}

func handleUpdatePowerRequest(w http.ResponseWriter, r *http.Request) {
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Println(err)
		return
	}
	var request lightbulbs.PowerRequest
	json.Unmarshal(body, &request)
	lightbulb := lightbulbs.UpdatePower(request)
	writeEmptyResponse(w)
	go broadCastToSockets(lightbulb)
}
func handleUpdateNameRequest(w http.ResponseWriter, r *http.Request) {
	writeEmptyResponse(w)
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
	writeEmptyResponse(w)
}

var websocketclients = make(map[*websocket.Conn]bool)
var upgrader = websocket.Upgrader{}

func handleWebsocketConnection(w http.ResponseWriter, r *http.Request) {
    ws, err := upgrader.Upgrade(w, r, nil)
    if err != nil {
        log.Println(err)
    }
    websocketclients[ws] = true
}

var mutex = &sync.Mutex{}

func broadCastToSockets(lightbulb lightbulbs.Lightbulb) {
	mutex.Lock()
    for client, _ := range websocketclients {
        err := client.WriteJSON(lightbulb)
        if err != nil {
        	log.Println(err)
        	delete(websocketclients, client)
		}
    }
	mutex.Unlock()
}

func main() {
	go lightbulbs.UdpListener()
	go broadcastForLighbulbsThread()

	fs := http.FileServer(http.Dir("../web/public/"))
	http.Handle("/", fs)
	http.HandleFunc("/ws", handleWebsocketConnection)
	http.HandleFunc("/lightbulb/update/ct", handleUpdateCtRequest)
	http.HandleFunc("/lightbulb/update/hsv", handleUpdateHsvRequest)
	http.HandleFunc("/lightbulb/update/power", handleUpdatePowerRequest)
	http.HandleFunc("/lightbulb/update/name", handleUpdateNameRequest)
	http.HandleFunc("/lightbulb/list", handleListRequest)
	http.HandleFunc("/poweroff", handlePoweroffRequest)
	fmt.Println(http.ListenAndServe(":8080", nil))
}
