"use client";

import React, {useEffect, useState} from "react";
import {Event} from "@/app/models/event";
import {getAllEvents} from "@/app/api/eventApi";
import ToggleEvent from "@/app/components/buttons/eventToggleButton";
import {sendEvent} from "@/app/api/eventWebSocket";
import ErrorMessage from "@/app/components/modals/errorMessage";

export default function SelectEvent({eventViewToggle}: { eventViewToggle: () => void }) {
    const [allEvents, setAllEvents] = useState<Event[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function fetchEvents() {
            try {
                const data = await getAllEvents();
                setAllEvents(data);
            } catch (err: unknown) {
                if (err instanceof Error) {
                    setError(err.message);
                } else {
                    setError("An unknown error occurred when fetching events.");
                }
            }
        }

        fetchEvents();
    }, []);

    function onStatusChange(event: Event) {
        sendEvent(event);
        eventViewToggle();
    }

    return (
        <main className="container my-5">
            <div className="card shadow-sm">
                <div className="card-header">
                    <h3 className="mb-0">All Created Events</h3>
                </div>
                <div className="card-body">
                    <ErrorMessage errorMessage={error}/>
                    {allEvents.length === 0 ? (
                        <p className="text-muted">No Events Found</p>
                    ) : (
                        <ul className="list-group">
                            {allEvents.map((event, idx) => (
                                <li key={event.id ?? idx} className="list-group-item">
                                    <div className="d-flex flex-column">
                                        <div>
                                            <strong>Name:</strong> {event.name}
                                        </div>
                                        <div>
                                            <strong>Status:</strong> {event.status}
                                        </div>
                                        <div>
                                            <strong>Creation Date:</strong> {new Date(event.startTime).toDateString()}
                                        </div>
                                        <div>
                                            <strong>Runtime
                                                Left:</strong> {(event.remainingDuration / 60000).toFixed(2)} Minutes
                                        </div>
                                    </div>
                                    <div className="mt-2">
                                        <ToggleEvent event={event} onStatusChange={onStatusChange}/>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </main>
    );
}
