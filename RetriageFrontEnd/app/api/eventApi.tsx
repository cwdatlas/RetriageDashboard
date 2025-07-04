import {Event} from "./../models/event";
import {EventTmp} from "@/app/models/eventTmp";
import Cookies from "js-cookie";

export const dynamic = 'force-static'
let API_BASE_URL = "";
const ENDPOINT = "/api/events"

// Helper to resolve the domain safely
function GetDomain(): void {
    const domain = Cookies.get("domain") || "localhost"
    API_BASE_URL = "https://" + domain;
}


export async function getAllEvents(): Promise<Event[]> {
    GetDomain()
    const res = await fetch(`${API_BASE_URL}` + ENDPOINT, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        throw new Error(`Failed to fetch patients: ${res.statusText}`);
    }
    return res.json();
}

/**
 * Create a new event
 */
export async function createEvent(event: EventTmp): Promise<EventTmp> {
    GetDomain()
    const res = await fetch(`${API_BASE_URL}` + ENDPOINT, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(event),
    });

    if (!res.ok) {
        throw new Error(`Failed to create event: ${res.statusText}`);
    }
    return res.json();
}

/**
 * Optionally, delete a event
 */
export async function deleteEvent(id: number, setError: (error: string) => void): Promise<void> {
    GetDomain()
    const res = await fetch(`${API_BASE_URL}` + ENDPOINT + "/" + `${id}`, {
        method: "DELETE",
    });
    if (!res.ok) {
        setError(`Failed to delete event: ${res.statusText}`);
        return
    }
}

export async function getActiveEvent(setEvent: (event: Event | null) => void, setError: (error: string) => void): Promise<void> {
    GetDomain()
    const res = await fetch(`${API_BASE_URL}` + ENDPOINT + "/active", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (res.status == 404) {
        console.log('No active event found');
        setEvent(null);
    } else if (!res.ok) {
        setError('Failed to fetch event');
    } else {
        const activeEvent: Event = await res.json();
        setEvent(activeEvent);
    }

}



