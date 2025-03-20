"use client";

import React, { useState } from "react";
import { Event } from "@/app/models/event";
import { Status } from "@/app/enumerations/status";

/**
 * Props:
 *  - event: The event object from your parent component (includes id, name, status, etc.).
 *  - onStatusChange?: Optional callback your parent can provide if you want to notify the parent or call an API.
 */
export default function ToggleEvent({
                                        event,
                                        onStatusChange,
                                    }: {
    event: Event;
    onStatusChange?: (eventId: number, UpdatedEvent: Event) => void;
}) {
    // Keep track of the status in local state
    const [localStatus, setLocalStatus] = useState(event.status);

    // Decide how to flip the status
    const isPaused = localStatus === Status.Paused;

    // Function to handle button clicks
    function handleToggle() {
        // Compute the new status
        const newStatus = isPaused ? Status.Running : Status.Paused;

        // 1) Update our local (front‐end) state so the UI is immediate
        setLocalStatus(newStatus);

        // 2) Optionally notify our parent or call an API from the parent
        //    This passes the event ID + new status back upstream
        if (onStatusChange && event.id != null) {
            event.status = newStatus;
            onStatusChange(event.id, event);
        }
    }

    return (
        <main style={{ border: "1px solid #ccc", padding: "1rem", margin: "1rem 0" }}>
            <button onClick={handleToggle}>
                {isPaused ? "Start Running" : "Pause Event"}
            </button>
        </main>
    );
}
