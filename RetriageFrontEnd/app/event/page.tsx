'use client'

import React, {useEffect, useState} from "react";

import Header from "@/app/components/panel/header";
import Footer from "@/app/components/panel/footer";
import SelectEvent from "@/app/components/panel/selectEvent";
import NurseWaitPage from "@/app/components/pages/nurseWaitPage";
import GuestWaitPage from "@/app/components/pages/guestWaitPage";
import {Event} from "@/app/models/event";
import {getActiveEvent as getCurrentActiveEvent} from "@/app/api/eventApi";
import EventVisualization from "@/app/components/pages/eventVisualization";
import EventNavBar from "@/app/components/panel/eventNavBar";
import ErrorMessage from "@/app/components/modals/errorMessage";
import {Role} from "@/app/enumerations/role";
import {useConnectEventWebSocket} from "@/app/api/eventWebSocket";
import {UserDto} from "@/app/models/userDto";
import {getUserByToken} from "@/app/api/userApi";

export default function EventViewer() {
    // Instead of reading the cookie immediately, we use local state.
    const [viewEvents, setViewEvents] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [activeEvent, setActiveEvent] = useState<Event | null>(null);
    const [user, setUser] = useState<UserDto | null>(null);

    // Fetch the active event when the component mounts.
    useEffect(() => {
        getCurrentActiveEvent(setActiveEvent, setError);
        void getUserByToken(setUser, setError);
    }, []);

    // Connect the WebSocket for real-time updates.
    useConnectEventWebSocket(setActiveEvent, setError);

    function toggleEventView() {
        setViewEvents(!viewEvents);
    }

    function getActiveEvent(): Event {
        if (activeEvent == null) {
            throw new Error("When calling getActiveEvent, event was found to be null.");
        }
        return activeEvent;
    }

    // While waiting for the client to mount and role to be set, return null (or a loading indicator).
    if (!user?.role) {
        return null;
    }

    return (
        <main className="d-flex flex-column min-vh-100">
            <Header/>
            {/* Mini Nav Bar always visible under header */}
            <EventNavBar activeEvent={activeEvent} toggleEventView={toggleEventView} getActiveEvent={getActiveEvent}/>
            <ErrorMessage errorMessage={error}/>
            {user.role === Role.Director && (
                <div className="container mt-3">
                    {viewEvents && <SelectEvent eventViewToggle={toggleEventView}/>}
                    {activeEvent != null && (
                        <div>
                            <EventVisualization getActiveEvent={getActiveEvent}/>
                        </div>
                    )}
                </div>
            )}
            {user.role === Role.Nurse && (
                <div className="container mt-3">
                    {activeEvent == null && <NurseWaitPage/>}
                    {activeEvent != null && (
                        <div>
                            <EventVisualization getActiveEvent={getActiveEvent}/>
                        </div>
                    )}
                </div>
            )}
            {user.role === Role.Guest && (
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
