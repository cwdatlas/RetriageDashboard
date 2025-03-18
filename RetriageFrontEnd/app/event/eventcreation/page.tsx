"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

import Header from "@/app/components/header";
import Footer from "@/app/components/footer";

// 1) Import your resource-template function
import {createResourceTemplate, getAllResourceTemplates} from "@/app/api/resourceTemplateApi";
import { createEvent } from "@/app/api/eventApi";

import { PatientPool } from "@/app/models/patientPool";
import { User } from "@/app/models/user";
import { Event } from "@/app/models/event";
import { Status } from "@/app/enumerations/status";
import {getCookies} from "@/app/api/cookieApi"
import {Role} from "@/app/enumerations/role";

export default function EventCreation() {
    const router = useRouter();

    // ---------- Main Event Form Fields ----------
    const [name, setName] = useState("");
    const [endTime, setEndTime] = useState("");
    const [error, setError] = useState<string | null>(null);

    // PatientPool Saving Handles
    const [resourceName, setResourceName] = useState("");
    const [patientProcessTime, setPatientProcessTime] = useState("");

    // Director + Resources for this new event
    const director: User = {
        firstName: getCookies("firstName"),
        lastName: getCookies("lastName"),
        email: getCookies("email"),
        role: getCookies("role") as Role,
    };

    // 2) State to hold *all* resource templates from your API
    const [allTemplates, setAllTemplates] = useState<PatientPool[]>([]);

    // 3) State for the user-selected Resources (the ones actually going into the event)
    const [selectedResources, setSelectedResources] = useState<PatientPool[]>([]);

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

    // ------ EVENT FORM SUBMISSION ------
    async function handleSubmitEvent(e: React.FormEvent) {
        e.preventDefault();

        if (!director) {
            setError("Director not loaded yet. Please wait or refresh.");
            return;
        }
        // For the demo, just using Date.now(); you would likely use an actual timestamp
        const startTime = 0;
        const endTimeNumeric = 1000;
        const eventStatus = Status.Paused;

        for(const checkResource of selectedResources) {
            checkResource.id = undefined;
        }

        const newEvent: Event = {
            name: name,
            director: director,
            resources: selectedResources,
            startTime: startTime,
            endTime: endTimeNumeric,
            status: eventStatus,
        };

        try {
            await createEvent(newEvent);
            router.push("/");
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message || "An error occurred");
            } else {
                setError("An unknown error occurred");
            }
        }
    }

    // ------ EVENT FORM SUBMISSION ------
    async function handleSubmitResource(e: React.FormEvent) {
        e.preventDefault();
        const newResource: PatientPool = {
            active: true,
            patientQueue: [],
            processTime: parseInt(patientProcessTime),
            usable: true,
            name: resourceName
        };

        try {
            await createResourceTemplate(newResource);
            router.push("/");
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message || "An error occurred");
            } else {
                setError("An unknown error occurred");
            }
        }
        const data = await getAllResourceTemplates();
        setAllTemplates(data);
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
                                    Name: {template.name}
                                    <input
                                        type="checkbox"
                                        checked={selectedResources.some((res) => res.id === template.id)}
                                        onChange={(e) => {
                                            if (e.target.checked) {
                                                // If box is now checked, add the resource to selectedResources
                                                setSelectedResources((prev) => [...prev, template]);
                                            } else {
                                                // If box is unchecked, remove the resource from selectedResources
                                                setSelectedResources((prev) =>
                                                    prev.filter((res) => res.id !== template.id)
                                                );
                                            }
                                        }}
                                    />
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
            <form onSubmit={handleSubmitResource}>
                {/* ========== CREATE MEDICAL RESOURCES ========== */}
                <div style={{ marginBottom: "1rem" }}>
                    <h3>Create Medical Resource:</h3>
                    <div style={{ marginBottom: "1rem" }}>
                        <label htmlFor="resourceName">Name: </label>
                        <input
                            id="resourceName"
                            type="text"
                            value={resourceName}
                            onChange={(e) => setResourceName(e.target.value)}
                            required
                        />
                    </div>
                    <div style={{ marginBottom: "1rem" }}>
                        <label htmlFor="patientProcessTime">Patient Process Time: </label>
                        <input
                            id="patientProcessTime"
                            type="text"
                            value={patientProcessTime}
                            onChange={(e) => setPatientProcessTime(e.target.value)}
                            required
                        />
                    </div>
                </div>
                <button type="submit" style={{ marginBottom: "2rem" }}>
                    Create Resource
                </button>
            </form>

            <Footer />
        </div>
    );
}
