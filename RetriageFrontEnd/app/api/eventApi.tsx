import {Event} from "./../models/event";
import {EventTmp} from "@/app/models/eventTmp";

export const dynamic = 'force-static'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const ENDPOINT = "/api/events"

export async function getAllEvents(): Promise<Event[]> {
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
 * Optionally, get a single event by ID
 */
export async function getEventById(): Promise<Event> {
    const res = await fetch(`${API_BASE_URL}` + ENDPOINT, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        throw new Error(`Failed to fetch event`);
    }
    return res.json();
}

/**
 * Optionally, delete a event
 */
export async function deleteEvent(id: number): Promise<void> {
    const res = await fetch(`${API_BASE_URL}` + ENDPOINT + `${id}`, {
        method: "DELETE",
    });
    if (!res.ok) {
        throw new Error(`Failed to delete event`);
    }
}
//TODO fix update event to actually work
export async function updateEvent(newEvent: Event): Promise<void> {
    const res = await fetch(`${API_BASE_URL}` + ENDPOINT, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(newEvent),
    });
    if (!res.ok) {
        throw new Error(`Failed to update event`);
    }
}



