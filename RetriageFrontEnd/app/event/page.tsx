'use client'

import {GetCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role";
import React, {useEffect, useState} from "react";
import SelectEvent from "@/app/components/panel/selectEvent";
import NurseWaitPage from "@/app/components/pages/nurseWaitPage";
import GuestWaitPage from "@/app/components/pages/guestWaitPage";
import Header from "@/app/components/panel/header";
import Footer from "@/app/components/panel/footer";
import ToggleButton from "@/app/components/buttons/toggleButton";
import CreatePatient from "@/app/components/buttons/createPatient";
import {Status} from "@/app/enumerations/status";
import {Event} from "@/app/models/event";
import {sendEvent, useConnectEventWebSocket} from "@/app/api/eventWebSocket";
import {getActiveEvent as getCurrentActiveEvent} from "@/app/api/eventApi";
import {User} from "@/app/models/user";
import Link from "next/link";
import EventVisualization from "@/app/components/pages/eventVisualization";

export default function EventViewer() {
    const role = GetCookies("role") as Role;
    const [viewEvents, setViewEvents] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [activeEvent, setActiveEvent] = useState<Event | null>(null);

    // Fetch the active event once when the component mounts
    useEffect(() => {
        getCurrentActiveEvent(setActiveEvent, setError);
    }, []);

    useEffect(() => {
        console.log("Active event initialized: ", activeEvent != null);
        if (activeEvent) {
            const user = activeEvent.nurses.find(user => user.email === GetCookies("email"));
            console.log("New User ", user != null, ". Data of: ", user);
            if (!user) {
                const newUser: User = {
                    email: GetCookies("email"),
                    firstName: GetCookies("firstName"),
                    lastName: GetCookies("lastName"),
                    role: role,
                }
                activeEvent.nurses.push(newUser)
                sendEvent(activeEvent)
            }
        }
    })

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
                    <div>This is the Admin event page</div>
                    <p><Link href={"/event/eventcreation"}>Create Event</Link></p>
                    <ToggleButton onToggle={toggleEventView} label={"Toggle Event Selection"}/>
                    {viewEvents && <SelectEvent eventViewToggle={toggleEventView}/>}
                    {activeEvent != null && (
                        <div>
                            {activeEvent.status === Status.Running && <CreatePatient getActiveEvent={getActiveEvent}/>}
                            {<EventVisualization getActiveEvent={getActiveEvent}/>}
                        </div>
                    )}
                </div>
            )}
            {role === Role.Nurse && (
                <div>
                    {activeEvent == null && <NurseWaitPage/>}
                    {activeEvent != null && (
                        <div>
                            {activeEvent.status === Status.Running && <CreatePatient getActiveEvent={getActiveEvent}/>}
                            {<EventVisualization getActiveEvent={getActiveEvent}/>}
                        </div>
                    )}
                </div>
            )}
            {role == Role.Guest && (
                <div>
                    {activeEvent == null && <GuestWaitPage/>}
                    {activeEvent != null && (
                        <div>
                            {activeEvent.status === Status.Running && <CreatePatient getActiveEvent={getActiveEvent}/>}
                            {<EventVisualization getActiveEvent={getActiveEvent}/>}
                        </div>
                    )}
                </div>
            )}
            <Footer/>
        </main>
    );
}
