'use client'

import Header from "@/app/components/header";
import Footer from "@/app/components/footer";
import {getCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role"
import Link from "next/link";
import React, {useEffect, useState} from "react";
import {getAllEvents} from "@/app/api/eventApi";
// @ts-ignore
import {Event} from "@/app/models/event";

export default function Event() {
    const role = getCookies("role");
    const [allEvents, setAllEvents] = useState<Event[]>([]);
    const [error, setError] = useState<string | null>(null);


    useEffect(() => {
        async function fetchEvents() {
            try {
                const data = await getAllEvents(); // your API call
                setAllEvents(data);
            } catch (err: unknown) {
                if (err instanceof Error) {
                    setError(err.message);
                } else {
                    setError("An unknown error occurred when fetching templates");
                }
            }
        }

        fetchEvents();
    }, []);

    if (role === Role.Director) {
        return (
            <main>
                <Header/>
                <div>This is the Admin event page</div>
                <Link href={"/event/eventcreation"}>Create Event</Link>

                <div style={{ marginBottom: "1rem" }}>
                    <h3>All Created Events:</h3>
                    <div>{error}</div>
                    {allEvents.length === 0 ? (
                        <p>No Events Found</p>
                    ) : (
                        <ul>
                            {allEvents.map((event, idx) => (
                                <li key={event.id ?? idx}>
                                    <div>Name: {event.name}</div>
                                    <div>Event Status: {event.status}</div>
                                    <div>Event Director: {event.director.firstName} {event.director.lastName}</div>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>

                <Footer/>
            </main>
        )
    }else if(role === Role.Nuse){
        return (
            <main>
                <Header/>
                <div>This is the event page</div>
                There is no active event for you to participate in.
                <Footer/>
            </main>
        )
    }else{
        <div>You are a guest, you do not have permission to view this page</div>
    }
}