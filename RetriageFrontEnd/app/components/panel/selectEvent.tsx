"use client";

import React, { useEffect, useState } from "react";
import { Event } from "@/app/models/event";
import { deleteEvent, getAllEvents } from "@/app/api/eventApi";
import ToggleEvent from "@/app/components/buttons/eventToggleButton";
import { sendEvent } from "@/app/api/eventWebSocket";
import ErrorMessage from "@/app/components/modals/errorMessage";
import DeleteEventButton from "@/app/components/buttons/deleteEventButton";
import { Status } from "@/app/enumerations/status";

export default function SelectEvent({ eventViewToggle }: { eventViewToggle: () => void }) {
    const [allEvents, setAllEvents] = useState<Event[]>([]);
    const [active, setActive] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Fetch events on mount
    useEffect(() => {
        async function fetchEvents() {
            try {
                const data = await getAllEvents();
                setAllEvents(data);
                // Derive active status based on the fetched data
                const runningEvents = data.filter((event) => event.status === Status.Running);
                if (runningEvents.length > 1) {
                    setError("There is more than one active event!! This should not be possible");
                    setActive(false);
                } else if (runningEvents.length === 1) {
                    setActive(true);
                } else {
                    setActive(false);
                }
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

    // Alternatively, you can derive the active status whenever allEvents changes:
    useEffect(() => {
        const runningEvents = allEvents.filter((event) => event.status === Status.Running);
        if (runningEvents.length > 1) {
            setError("There is more than one active event!! This should not be possible");
            setActive(false);
        } else if (runningEvents.length === 1) {
            setActive(true);
        } else {
            setActive(false);
        }
    }, [allEvents]);

    function onStatusChange(event: Event) {
        sendEvent(event);
        eventViewToggle();
        // Also update active status based on the new event status
        if (event.status === Status.Running) {
            setActive(true);
        }
    }

    async function deleteHandler(id: number) {
        setError(null);
        try {
            // Await the API call to delete the event.
            await deleteEvent(id, setError);
            // Update the state by creating a new array without the deleted event.
            setAllEvents((events) => events.filter((event) => event.id !== id));
        } catch (err) {
            if (err instanceof Error) {
                setError(err.message);
            } else {
                setError("An unknown error occurred");
            }
        }
    }

    return (
        <main className="container my-5">
            <div className="card shadow-sm">
                <div className="card-header">
                    <h3 className="mb-0">All Created Events</h3>
                </div>
                <div className="card-body">
                    <ErrorMessage errorMessage={error} />
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
                                            <strong>Creation Date:</strong>{" "}
                                            {new Date(event.startTime).toDateString()}
                                        </div>
                                        <div>
                                            <strong>Runtime Left:</strong>{" "}
                                            {(event.remainingDuration / 60000).toFixed(2)} Minutes
                                        </div>
                                    </div>
                                    <div className="mt-2">
                                        <ToggleEvent event={event} onStatusChange={onStatusChange} active={active} />
                                    </div>
                                    {event.id && (
                                        <div className="mt-2">
                                            <DeleteEventButton id={event.id} deleteHandler={deleteHandler} />
                                        </div>
                                    )}
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </main>
    );
}
