"use client";

import React from "react";
import {Event} from "@/app/models/event";
import {Status} from "@/app/enumerations/status";

/**
 * Props:
 *  - event: The event object from your parent component (includes id, name, status, etc.).
 *  - onStatusChange?: Optional callback your parent can provide if you want to notify the parent or call an API.
 */
export default function ToggleEvent({event, onStatusChange, active}: {
    event: Event; onStatusChange?: (UpdatedEvent: Event) => void; active : boolean
}) {

    // Function to handle button clicks
    function handleToggle() {
        // Compute the new status
        const newStatus = event.status === Status.Running ? Status.Paused : Status.Running;

        // 2) Optionally notify our parent or call an API from the parent
        //    This passes the event ID + new status back upstream
        if (onStatusChange && event.id != null) {
            event.status = newStatus;
            event.timeOfStatusChange = Date.now();
            onStatusChange(event);
        }
    }

    return (
        <main style={{border: "1px solid #ccc", padding: "1rem", margin: "1rem 0"}}>
            <button className="btn btn-primary" onClick={handleToggle} disabled={event.status !== Status.Running && active}>
                {event.status === Status.Running && ("Pause")}
                {event.status === Status.Created && ("Start")}
                {event.status === Status.Paused && ("Resume")}
                {event.status === Status.Ended && ("Restart")}
            </button>
        </main>
    );
}
