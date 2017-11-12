package lightbulbs

import (
	"fmt"
	"net"
	"log"
	"strings"
	"strconv"
	"time"
)

const BROADCAST_ADDRESS = "239.255.255.250:1982"
const BROADCAST_MESSAGE = "M-SEARCH * HTTP/1.1\r\n" +
	"HOST: 239.255.255.250:1982\r\n" +
	"MAN: \"ssdp:discover\"\r\n" +
	"ST: wifi_bulb"
type ColorMode int
const (
	COLOR_TEMPERATURE = 1
	COLOR_MODE = 2
)

type Lightbulb struct {
	Id int64
	Location string
	Power string
	Bright int64
	Ct int64
	Hue int64
	Sat int64
	ColorMode ColorMode
	Name string
	IsActive bool
	LastChangeMillis int64
}

type HsvRequest struct {
	Id int64
	Hue int64
	Sat int64
	Bright int64
}

type CtRequest struct {
	Id int64
	Ct int64
	Bright int64
}

type PowerRequest struct {
	Id int64
	Power string
}

type NameRequest struct {
	Id int64
	Name int64
}

var addr *net.UDPAddr
var conn *net.UDPConn

var lightbulbList map[int64]Lightbulb

func init() {
	tmpAddr, err := net.ResolveUDPAddr("udp", BROADCAST_ADDRESS)
	if err != nil {
		log.Fatal(err)
		return
	}
	addr = tmpAddr
	c, err := net.ListenUDP("udp", nil)
	if err != nil {
		log.Fatal(err)
		return
	}
	conn = c
	lightbulbList = make(map[int64]Lightbulb)
}

func BroadcastForLightbulbs() {
	conn.WriteToUDP([]byte(BROADCAST_MESSAGE), addr)
}

func UdpListener() {
	for {
		buf := make([]byte, 2048)
		n, _, err := conn.ReadFromUDP(buf)
		if err != nil {
			fmt.Println(err)
		}
		parseMessageAndStoreLightbulb(string(buf[0:n]))
	}
}

func parseMessageAndStoreLightbulb(message string) {
	rowMap := make(map[string]string)
	for _, row := range strings.Split(message, "\r\n") {
		if strings.Contains(row, ":") {
			splitRow := strings.SplitN(row, ":", 2)
			rowMap[strings.TrimSpace(splitRow[0])] = strings.TrimSpace(splitRow[1])
		}
	}
	id, err := strconv.ParseInt(rowMap["id"], 0, 32)
	if err != nil {
		log.Fatal(err)
		return
	}
	bright, _:= strconv.ParseInt(rowMap["bright"], 10, 32)
	ct, _ := strconv.ParseInt(rowMap["ct"], 10, 32)
	hue, _ := strconv.ParseInt(rowMap["hue"], 10, 32)
	sat, _ := strconv.ParseInt(rowMap["sat"], 10, 32)
	var colorMode ColorMode
	if rowMap["color_mode"] == "2" {
		colorMode = COLOR_TEMPERATURE
	} else {
		colorMode = COLOR_MODE
	}
	lightbulbList[id] = Lightbulb{
		Id: id,
		Location: strings.Replace(rowMap["Location"], "yeelight://", "", 1),
		Power: rowMap["power"],
		Bright: bright,
		Ct: ct,
		Hue: hue,
		Sat: sat,
		ColorMode: colorMode,
		Name: rowMap["name"],
		IsActive: true,
		LastChangeMillis: time.Now().Unix(),
	}
}

func GetLightbulbs() []Lightbulb {
	list := make([]Lightbulb, 0, len(lightbulbList))
	for _, value := range lightbulbList {
		list = append(list, value)
	}
	return list
}

func PowerOffAll() {
	for _, value := range lightbulbList {
		UpdatePower(PowerRequest{value.Id, "off"})
	}
}

func UpdatePower(request PowerRequest) {
	requestString := fmt.Sprintf(
		"{\"id\": 1, " +
			"\"method\": \"set_power\", " +
			"\"params\":[\"%s\"]}\r\n",
		request.Power)
	sendRequest(request.Id, requestString)
}

func UpdateHsv(request HsvRequest) {
	requestString := fmt.Sprintf(
		"{\"id\": 1, " +
			"\"method\": \"set_scene\", " +
			"\"params\":[\"hsv\", %d, %d, %d]}\r\n",
		request.Hue, request.Sat, request.Bright)
	sendRequest(request.Id, requestString)
}

func UpdateCt(request CtRequest) {
	requestString := fmt.Sprintf(
		"{\"id\": 1, " +
			"\"method\": \"set_scene\", " +
			"\"params\":[\"ct\", %d, %d]}\r\n",
		request.Ct, request.Bright)
	sendRequest(request.Id, requestString)
}

func UpdateName() {

}

func sendRequest(id int64, request string) {
	bulb := lightbulbList[id]
	bulbConn, err := net.Dial("tcp", bulb.Location)
	if err != nil {
		fmt.Println(err)
	}
	bulbConn.Write([]byte(request))
	bulbConn.Close()
}
