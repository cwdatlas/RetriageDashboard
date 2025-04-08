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

    useEffect(() => {
        async function fetchEvents() {
            try {
                const data = await getAllEvents();
                setAllEvents(data);

                // Example logic deriving if an event is active
                const runningEvents = data.filter(e => e.status === Status.Running);
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

    function onStatusChange(event: Event) {
        sendEvent(event);
        eventViewToggle();

        if (event.status === Status.Running) {
            setActive(true);
        }
    }

    async function deleteHandler(id: number) {
        setError(null);
        try {
            await deleteEvent(id, setError);
            setAllEvents(events => events.filter(ev => ev.id !== id));
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
                                    {/*
                      Use d-flex to arrange content in a row
                      Use justify-content-between to place info on the left & buttons on the right
                      align-items-center keeps them vertically aligned
                  */}
                                    <div className="d-flex justify-content-between align-items-center">
                                        {/* Left side: event info */}
                                        <div>
                                            <strong>Name:</strong> {event.name}
                                            <br />
                                            <strong>Status:</strong> {event.status}
                                            <br />
                                            <strong>Creation Date:</strong>{" "}
                                            {new Date(event.startTime).toDateString()}
                                            <br />
                                            <strong>Runtime Left:</strong>{" "}
                                            {(event.remainingDuration / 60000).toFixed(2)} Minutes
                                        </div>

                                        {/* Right side: buttons */}
                                        <div>
                                            {/*
                        You can optionally group your buttons with Bootstrap’s "btn-group",
                        but here they’re just placed next to each other with some spacing.
                      */}
                                            <ToggleEvent
                                                event={event}
                                                onStatusChange={onStatusChange}
                                                active={active}
                                            />
                                            {event.id && (
                                                <DeleteEventButton
                                                    id={event.id}
                                                    deleteHandler={deleteHandler}
                                                />
                                            )}
                                        </div>
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
