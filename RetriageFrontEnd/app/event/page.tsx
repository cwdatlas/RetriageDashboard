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

// MiniNavBar component: a sticky nav bar with buttons and a countdown timer.
function MiniNavBar({
                        activeEvent,
                        toggleEventView,
                        getActiveEvent,
                    }: {
    activeEvent: Event | null;
    toggleEventView: () => void;
    getActiveEvent: () => Event;
}) {
    const [timeLeft, setTimeLeft] = useState<number>(0);
    const [role] = useState(GetCookies("role") as Role);

    // Start countdown only if event is running and has a startTime and duration.
    useEffect(() => {
        if (activeEvent && activeEvent.status === Status.Running && activeEvent.startTime && activeEvent.duration) {
            const targetTime = activeEvent.startTime + activeEvent.duration;
            const interval = setInterval(() => {
                const now = Date.now();
                const remaining = targetTime - now;
                setTimeLeft(remaining > 0 ? remaining : 0);
            }, 1000);
            return () => clearInterval(interval);
        }
    }, [activeEvent]);

    // Simple formatter to show mm:ss
    const formatTime = (ms: number) => {
        const totalSeconds = Math.floor(ms / 1000);
        const minutes = Math.floor(totalSeconds / 60);
        const seconds = totalSeconds % 60;
        return `${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-secondary sticky-top" style={{zIndex: 1050}}>
            <div className="container-fluid">
                {/* Left side: toggle event section button */}
                {role == Role.Director &&
                    <div className="d-flex">
                        <ToggleButton onToggle={toggleEventView} label={"Toggle Event Section"}/>
                    </div>}
                {/* Center: countdown timer */}
                <div className="mx-auto">
                    {activeEvent && activeEvent.status === Status.Running ? (
                        <span className="navbar-text h5 mb-0">{formatTime(timeLeft)}</span>
                    ) : (
                        <span className="navbar-text h5 mb-0">Event Not Running</span>
                    )}
                </div>
                {/* Right side: Create Patient (if event running) and Create Event */}
                <div className="d-flex">
                    {activeEvent && activeEvent.status === Status.Running && (
                        <div className="me-2">
                            {activeEvent.status === Status.Running && role != Role.Guest &&
                                <CreatePatient getActiveEvent={getActiveEvent}/>}
                        </div>
                    )}
                    <div>
                        {role == Role.Director && <Link className="btn btn-primary" href="/event/eventcreation">
                            Create Event
                        </Link>}
                    </div>
                </div>
            </div>
        </nav>
    );
}

export default function EventViewer() {
    const role = GetCookies("role") as Role;
    const [viewEvents, setViewEvents] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [activeEvent, setActiveEvent] = useState<Event | null>(null);

    // Fetch the active event when the component mounts.
    useEffect(() => {
        getCurrentActiveEvent(setActiveEvent, setError);
    }, []);

    useEffect(() => {
        console.log("Active event initialized: ", activeEvent != null);
        if (activeEvent) {
            const user = activeEvent.nurses.find((user) => user.email === GetCookies("email"));
            console.log("New User ", user != null, ". Data of: ", user);
            if (!user) {
                const newUser: User = {
                    email: GetCookies("email"),
                    firstName: GetCookies("firstName"),
                    lastName: GetCookies("lastName"),
                    role: role,
                };
                activeEvent.nurses.push(newUser);
                sendEvent(activeEvent);
            }
        }
    });

    // Use custom hook to connect WebSocket updates.
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
        <main className="d-flex flex-column min-vh-100">
            <Header/>
            {/* Mini Nav Bar always visible under header */}
            <MiniNavBar activeEvent={activeEvent} toggleEventView={toggleEventView} getActiveEvent={getActiveEvent}/>
            {error && <p style={{color: "red"}}>{error}</p>}
            {role === Role.Director && (
                <div className="container mt-3">
                    {viewEvents && <SelectEvent eventViewToggle={toggleEventView}/>}
                    {activeEvent != null && (
                        <div>
                            <EventVisualization getActiveEvent={getActiveEvent}/>
                        </div>
                    )}
                </div>
            )}
            {role === Role.Nurse && (
                <div className="container mt-3">
                    {activeEvent == null && <NurseWaitPage/>}
                    {activeEvent != null && (
                        <div>
                            <EventVisualization getActiveEvent={getActiveEvent}/>
                        </div>
                    )}
                </div>
            )}
            {role == Role.Guest && (
                <div className="container mt-3">
                    {activeEvent == null && <GuestWaitPage/>}
                    {activeEvent != null && (
                        <div>
                            <EventVisualization getActiveEvent={getActiveEvent}/>
                        </div>
                    )}
                </div>
            )}
            <Footer/>
        </main>
    );
}
