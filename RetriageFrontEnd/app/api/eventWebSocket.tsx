import {useEffect} from 'react';
import {Event} from "@/app/models/event";
import {Client, IMessage} from "@stomp/stompjs";
import {ResponseWrapper} from "@/app/models/responseWrapper";
import Cookies from "js-cookie";

let API_BASE_URL = ""
const ENDPOINT = "/active_event";
const UPDATE_EVENT = "/ws/update";
const TOPIC = "/topic/event_updates";
let client: Client;

// Helper to resolve the domain safely
function GetDomain(): void {
    const domain = Cookies.get("domain") || "localhost"
    API_BASE_URL = "https://" + domain;
}

export function useConnectEventWebSocket(setActiveEvent: (event: Event | null) => void, setError: (error: string) => void): void {
    GetDomain()
    useEffect(() => {
        // Create the STOMP client
        const stompClient = new Client({
            brokerURL: API_BASE_URL + ENDPOINT,
            reconnectDelay: 5000, // automatically attempt to reconnect if the connection is lost
            onConnect: () => {
                console.log("STOMP connected: ", stompClient.connected);
                // Subscribe to /topic/events
                stompClient.subscribe(TOPIC, (message: IMessage) => {
                    // The server broadcasts Event objects as JSON
                    const eventData: ResponseWrapper<Event> = JSON.parse(message.body);
                    if(eventData.httpStatus == 404){
                        setActiveEvent(null);
                    }else if(eventData.httpStatus == 400){
                        console.error(eventData.error);
                        setError(eventData.error);
                    } else if (eventData.data) {
                        console.log("Event Sent to front end: " + eventData.data.name);
                        setActiveEvent(eventData.data);
                    }
                });

            },
            onStompError: (frame) => {
                console.error("Broker reported error: " + frame.headers["message"]);
                console.error("Additional details: " + frame.body);
                setError("Error when receiving data to server.")
            },
        });

        // Activate the STOMP client
        stompClient.activate();
        client = stompClient;


        // Cleanup when component unmounts
        return () => {
            stompClient.deactivate();
        };
    }, []);
}

// Sends an event to the server
export function sendEvent(event: Event) {
    console.log("Sending event to be saved.");
    client.publish({destination: UPDATE_EVENT, body: JSON.stringify(event)});
}


