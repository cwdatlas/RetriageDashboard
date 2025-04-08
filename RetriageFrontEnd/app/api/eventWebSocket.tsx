import {useEffect} from 'react';
import {Event} from "@/app/models/event";
import {Client, IMessage} from "@stomp/stompjs";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "ws://localhost:8080";
const ENDPOINT = "/active_event";
const UPDATE_EVENT = "/ws/update";
const TOPIC = "/topic/event_updates";
let client: Client;

export function useConnectEventWebSocket(setActiveEvent: (event: Event | null) => void) {

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
                    const eventData: Event = JSON.parse(message.body);
                    if (eventData && eventData.name == "NoEventFound") {
                        setActiveEvent(null);
                    } else if (eventData) {
                        console.log("Event Sent to front end: " + eventData);
                        setActiveEvent(eventData);
                    }
                });

            },
            onStompError: (frame) => {
                console.error("Broker reported error: " + frame.headers["message"]);
                console.error("Additional details: " + frame.body);
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
    // TODO include error handling
    console.log("Sending event to be saved.");
    client.publish({destination: UPDATE_EVENT, body: JSON.stringify(event)});
}


