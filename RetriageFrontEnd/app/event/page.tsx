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

export default function EventViewer() {
    const role = getCookies("role") as Role;

    const [viewEvents, setViewEvents] = useState(true);

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


    return (
        <main>
            <Header/>
            {role === Role.Director && (
                <div>
                    <ToggleButton onToggle={toggleEventView} label={"Toggle Event Selection"}/>
                    {viewEvents && (<SelectEvent eventViewToggle={toggleEventView} setActiveEvent={setActiveEvent}/>)}
                    {activeEvent.status == Status.Running &&(<CreatePatient event={activeEvent}/>)}
                </div>
            )}
            {role === Role.Nuse && (
                <div>
                    {activeEvent.status == Status.Running &&(<CreatePatient event={activeEvent}/>)}
                    {activeEvent.status != Status.Running &&(<NurseWaitEventPage/>)}
                </div>

            )}
            {role != Role.Director || role != Role.Director && (
                <GuestWaitPage/>
            )}
            <Footer/>
        </main>
    )
}