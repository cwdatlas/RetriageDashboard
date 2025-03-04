"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

import Header from "@/app/components/header";
import Footer from "@/app/components/footer";

// 1) Import your resource-template function
import { getAllResourceTemplates } from "@/app/api/resourceTemplateApi";
import { createEvent } from "@/app/api/eventApi";

import { Resource } from "@/app/models/resource";
import { User } from "@/app/models/user";
import { Event } from "@/app/models/event";
import { Status } from "@/app/enumerations/status";
import { Role } from "@/app/enumerations/role";

export default function EventCreation() {
    const router = useRouter();

    // ---------- Main Event Form Fields ----------
    const [name, setName] = useState("");
    const [endTime, setEndTime] = useState("");
    const [error, setError] = useState<string | null>(null);

    // Director + Resources for this new event
    const director: User = {
        firstName: "Aidan",
        lastName: "Scott",
        email: "aidanscott001@gmail.com",
        role: Role.Director,
    };

    // 2) State to hold *all* resource templates from your API
    const [allTemplates, setAllTemplates] = useState<Resource[]>([]);

    // 3) State for the user-selected Resources (the ones actually going into the event)
    const [selectedResources, setSelectedResources] = useState<Resource[]>([]);

    // 4) Fetch *all* resource templates on mount
    useEffect(() => {
        async function fetchTemplates() {
            try {
                const data = await getAllResourceTemplates(); // your API call
                setAllTemplates(data);
            } catch (err: unknown) {
                if (err instanceof Error) {
                    setError(err.message);
                } else {
                    setError("An unknown error occurred when fetching templates");
                }
            }
        }

        fetchTemplates();
    }, []);

    // ------ Add a template to this event ------
    function handleAddResourceToEvent(resource: Resource) {
        setSelectedResources((prev) => [...prev, resource]);
    }

    // ------ Remove a resource from this event ------
    function handleRemoveResourceFromEvent(index: number) {
        setSelectedResources((prev) => {
            const newArr = [...prev];
            newArr.splice(index, 1);
            return newArr;
        });
    }

    // ------ EVENT FORM SUBMISSION ------
    async function handleSubmitEvent(e: React.FormEvent) {
        e.preventDefault();

        if (!director) {
            setError("Director not loaded yet. Please wait or refresh.");
            return;
        }
        // For the demo, just using Date.now(); you would likely use an actual timestamp
        const startTime = Date.now();
        const endTimeNumeric = Date.now() + 1000;
        const eventStatus = Status.Paused;

        const newEvent: Event = {
            name,
            director,
            resources: selectedResources,
            startTime,
            endTime: endTimeNumeric,
            status: eventStatus,
        };

        try {
            await createEvent({ event: newEvent });
            router.push("/");
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message || "An error occurred");
            } else {
                setError("An unknown error occurred");
            }
        }
    }

    return (
        <div>
            <Header />
            <h1>Create a New Event</h1>

            <form onSubmit={handleSubmitEvent}>
                {error && <p style={{ color: "red" }}>{error}</p>}

                {/* ========== EVENT NAME ========== */}
                <div style={{ marginBottom: "1rem" }}>
                    <label htmlFor="eventName">Event Name: </label>
                    <input
                        id="eventName"
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>
                <div style={{ marginBottom: "1rem" }}>
                    <label htmlFor="eventEndTime">End Time: </label>
                    <input
                        id="eventEndTime"
                        type="text"
                        value={endTime}
                        onChange={(e) => setEndTime(e.target.value)}
                        required
                    />
                </div>

                {/* ========== LIST OF ALL RESOURCE TEMPLATES ========== */}
                <div style={{ marginBottom: "1rem" }}>
                    <h3>All Resource Templates:</h3>
                    {allTemplates.length === 0 ? (
                        <p>Loading or none found...</p>
                    ) : (
                        <ul>
                            {allTemplates.map((template, idx) => (
                                <li key={template.id ?? idx}>
                                    {template.name} (ID: {template.id})
                                    <button
                                        type="button"
                                        onClick={() => handleAddResourceToEvent(template)}
                                        style={{ marginLeft: "1rem" }}
                                    >
                                        Add
                                    </button>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>

                {/* ========== SELECTED RESOURCES (EVENT) ========== */}
                <div style={{ marginBottom: "1rem" }}>
                    <h3>Event Resources:</h3>
                    {selectedResources.length === 0 ? (
                        <p>None selected</p>
                    ) : (
                        <ul>
                            {selectedResources.map((res, idx) => (
                                <li key={idx}>
                                    {res.name} (ID: {res.id})
                                    <button
                                        type="button"
                                        onClick={() => handleRemoveResourceFromEvent(idx)}
                                        style={{ marginLeft: "1rem" }}
                                    >
                                        Remove
                                    </button>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>

                {/* ========== SUBMIT EVENT BUTTON ========== */}
                <button type="submit" style={{ marginBottom: "2rem" }}>
                    Create Event
                </button>
            </form>

            <Footer />
        </div>
    );
}
