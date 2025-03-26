'use client'

import {getCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role"
import React, {useState} from "react";
import SelectEvent from "@/app/components/selectEvent";
import NurseWaitEventPage from "@/app/components/nurseWaitEventPage";
import GuestWaitPage from "@/app/components/guestWaitPage";
import Header from "@/app/components/header";
import Footer from "@/app/components/footer";
import ToggleButton from "@/app/components/toggleButton";
import CreatePatient from "@/app/components/patientCreator";
import {Status} from "@/app/enumerations/status";
import {Event} from "@/app/models/event";
import {ConnectEventWebSocket} from "@/app/api/eventWebSocket";

export default function EventViewer() {
    const role = getCookies("role") as Role;

    const [viewEvents, setViewEvents] = useState(true);
    const [error] = useState<string | null>(null);

    const defaultEvent: Event = {
        nurses: [],
        pools: [],
        startTime: 0,
        status: Status.Paused,
        name: "default",
        director: {
            firstName: "Guest",
            email: "",
            lastName: "",
            role: Role.Guest
        },
        duration: 0
    };
    const [activeEvent, setActiveEvent] = useState(defaultEvent);
    function toggleEventView(){
        setViewEvents(!viewEvents);
    }
    function activeEventSet(event : Event){
        setActiveEvent(event);
    }
    function getActiveEvent(): Event{
        return activeEvent;
    }

    ConnectEventWebSocket(activeEventSet);


    return (
        <main>
            <Header/>
            {error && <p style={{ color: "red" }}>{error}</p>}
            {role === Role.Director && (
                <div>
                    <ToggleButton onToggle={toggleEventView} label={"Toggle Event Selection"}/>
                    {viewEvents && (<SelectEvent eventViewToggle={toggleEventView}/>)}
                    {activeEvent.status == Status.Running &&(<CreatePatient getActiveEvent={getActiveEvent}/>)}
                </div>
            )}
            {role == Role.Nurse && (
                <div>
                    {activeEvent.status != Status.Running &&(<NurseWaitEventPage/>)}
                    {activeEvent.status == Status.Running &&(<CreatePatient getActiveEvent={getActiveEvent}/>)}
                </div>

            )}
            {role != Role.Director && role != Role.Nurse && (
                <GuestWaitPage/>
            )}
            <Footer/>
        </main>
    )
}