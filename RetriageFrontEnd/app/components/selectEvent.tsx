'use client'

import Link from "next/link";
import React, {useEffect, useState} from "react";
import {Event} from "@/app/models/event";
import {getAllEvents} from "@/app/api/eventApi";
import ToggleEvent from "@/app/components/eventToggleButton";
import {updateEvent} from "@/app/api/eventApi";

export default function SelectEvent({eventViewToggle, setActiveEvent} : {eventViewToggle: () => void; setActiveEvent: (newEvent: Event) => void;}) {
    const [allEvents, setAllEvents] = useState<Event[]>([]);
    const [error, setError] = useState<string | null>(null);


    useEffect(() => {
        async function fetchEvents() {
            try {
                const data = await getAllEvents(); // your API call
                setAllEvents(data);
            } catch (err: unknown) {
                if (err instanceof Error) {
                    setError(err.message);
                } else {
                    setError("An unknown error occurred when fetching templates");
                }
            }
        }

        fetchEvents();
    }, []);

    function onStatusChange(event: Event) {
        updateEvent(event)
            .catch((err) => {
            console.error("Failed to update event status:", err);
            // Possibly show an error message in the UI
        });
        setActiveEvent(event);
        eventViewToggle();
    }

    return (
            <main>
                <div>This is the Admin event page</div>
                <Link href={"/event/eventcreation"}>Create Event</Link>

                <div style={{ marginBottom: "1rem" }}>
                    <h3>All Created Events:</h3>
                    <div>{error}</div>
                    {allEvents.length === 0 ? (
                        <p>No Events Found</p>
                    ) : (
                        <ul>
                            {allEvents.map((event, idx) => (
                                <li key={event.id ?? idx}>
                                    <div>Name: {event.name}</div>
                                    <div>Status: {event.status}</div>
                                    <div>Creation Date: {new Date(event.startTime).toDateString()}</div>
                                    <div>Runtime Left: {event.duration / 60000} Minutes</div>
                                    <ToggleEvent event = {event} onStatusChange = {onStatusChange} />
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </main>
        )
}