'use client'

import {getCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role";
import React, {useEffect, useState} from "react";
import SelectEvent from "@/app/components/selectEvent";
import NurseWaitEventPage from "@/app/components/nurseWaitEventPage";
import GuestWaitPage from "@/app/components/guestWaitPage";
import Header from "@/app/components/header";
import Footer from "@/app/components/footer";
import ToggleButton from "@/app/components/toggleButton";
import CreatePatient from "@/app/components/createPatient";
import {Status} from "@/app/enumerations/status";
import {Event} from "@/app/models/event";
import {useConnectEventWebSocket} from "@/app/api/eventWebSocket";
import {getActiveEvent as getCurrentActiveEvent} from "@/app/api/eventApi";

export default function EventViewer() {
    const role = getCookies("role") as Role;
    const [viewEvents, setViewEvents] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [activeEvent, setActiveEvent] = useState<Event | null>(null);

    // Fetch the active event once when the component mounts
    useEffect(() => {
        getCurrentActiveEvent(setActiveEvent, setError);
    }, []);

    // Use the custom hook to connect the WebSocket
    useConnectEventWebSocket(setActiveEvent);

    function toggleEventView() {
        setViewEvents(!viewEvents);
    }

    function getActiveEvent(): Event {
        if (activeEvent == null) {
            throw new Error("When calling getActiveEvent, event was found to be null.");
        }
        return activeEvent;
    }

    return (
        <main>
            <Header/>
            {error && <p style={{color: "red"}}>{error}</p>}
            {role === Role.Director && (
                <div>
                    <ToggleButton onToggle={toggleEventView} label={"Toggle Event Selection"}/>
                    {viewEvents && <SelectEvent eventViewToggle={toggleEventView}/>}
                    {activeEvent != null && (
                        <div>
                            {activeEvent.status === Status.Running && <CreatePatient getActiveEvent={getActiveEvent}/>}
                        </div>
                    )}
                </div>
            )}
            {role === Role.Nurse && activeEvent != null && (
                <div>
                    {activeEvent.status !== Status.Running && <NurseWaitEventPage/>}
                    {activeEvent.status === Status.Running && <CreatePatient getActiveEvent={getActiveEvent}/>}
                </div>
            )}
            {role !== Role.Director && role !== Role.Nurse && <GuestWaitPage/>}
            <Footer/>
        </main>
    );
}
