"use client";

import React, {useEffect, useState} from "react";
import {useRouter} from "next/navigation";

import Header from "@/app/components/panel/header";
import Footer from "@/app/components/panel/footer";

// 1) Import your pool-template function
import {createPoolTemplate, getAllPoolTemplates} from "@/app/api/patientPoolTmpApi";
import {createEvent} from "@/app/api/eventApi";

import {User} from "@/app/models/user";
import {GetCookies} from "@/app/api/cookieApi"
import {Role} from "@/app/enumerations/role";
import {PoolType} from "@/app/enumerations/poolType";
import {PatientPoolTmp} from "@/app/models/patientPoolTmp";
import {EventTmp} from "@/app/models/eventTmp";
import UploadImagePanel from "@/app/components/panel/uploadImagePanel";

export default function EventCreation() {
    const router = useRouter();

    // ---------- Main Event Form Fields ----------
    const [name, setName] = useState("");
    const [duration, setDuration] = useState("");
    const [error, setError] = useState<string | null>(null);

    // PatientPool Saving Handles
    const [poolName, setPoolName] = useState("");
    const [poolType, setPoolType] = useState(PoolType.MedService);
    const [patientProcessTime, setPatientProcessTime] = useState("");

    // Director + Pools for this new event
    const director: User = {
        firstName: GetCookies("firstName"),
        lastName: GetCookies("lastName"),
        email: GetCookies("email"),
        role: GetCookies("role") as Role,
    };

    // 2) State to hold *all* pool templates from your API
    const [allTemplates, setAllTemplates] = useState<PatientPoolTmp[]>([]);

    // 3) State for the user-selected Pools (the ones actually going into the event)
    const [selectedPools, setSelectedPools] = useState<PatientPoolTmp[]>([]);

    // 4) Fetch *all* pool templates on mount
    useEffect(() => {
        async function fetchTemplates() {
            try {
                const data = await getAllPoolTemplates(); // your API call
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

        const newEvent: EventTmp = {
            name: name,
            director: director,
            poolTmps: selectedPools,
            duration: parseInt(duration) * 60000,
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
    async function handleSubmitPool(e: React.FormEvent) {
        e.preventDefault();
        const newPool: PatientPoolTmp = {
            queueSize: 0,
            poolType: poolType,
            processTime: parseInt(patientProcessTime) * 60000 || 60000,
            usable: true,
            name: poolName,
            poolNumber: 1
        };

        try {
            await createPoolTemplate(newPool);
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message || "An error occurred");
            } else {
                setError("An unknown error occurred");
            }
        }
        const data = await getAllPoolTemplates();
        setAllTemplates(data);
    }

    return (
        <div className="d-flex flex-column min-vh-100">
            <Header/>
            <h1>Create a New Event</h1>

            <form onSubmit={handleSubmitEvent}>
                {error && <p style={{color: "red"}}>{error}</p>}

                {/* ========== EVENT NAME ========== */}
                <div style={{marginBottom: "1rem"}}>
                    <label htmlFor="eventName">Event Name: </label>
                    <input
                        id="eventName"
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>
                <div style={{marginBottom: "1rem"}}>
                    <label htmlFor="eventDuration">Duration: </label>
                    <input
                        id="eventDuration"
                        type="text"
                        value={duration}
                        onChange={(e) => setDuration(e.target.value)}
                        required
                    /> minutes
                </div>

                {/* ========== LIST OF ALL RESOURCE TEMPLATES ========== */}
                <div style={{ marginBottom: "1rem" }}>
                    <h3>All Pool Templates:</h3>
                    {allTemplates.length === 0 ? (
                        <p>Loading or none found...</p>
                    ) : (
                        <ul>
                            {allTemplates.map((template, idx) => {
                                // Check if this template is in selectedPools. If so, get its poolNumber and queueSize; otherwise, default to 0.
                                const existing = selectedPools.find((res) => res.name === template.name);
                                const currentPoolNumber = existing?.poolNumber ?? 0;
                                const currentQueueSize = existing?.queueSize ?? 0;

                                return (
                                    <li key={template.name ?? idx}>
                                        <div>
                                            Name: {template.name} &nbsp; Type: {template.poolType}
                                        </div>
                                        <label>
                                            Pool Number:{" "}
                                            <select
                                                value={currentPoolNumber}
                                                onChange={(e) => {
                                                    const newNumber = parseInt(e.target.value, 10);
                                                    if (newNumber === 0) {
                                                        // Remove from selectedPools if 0 is chosen.
                                                        setSelectedPools((prev) =>
                                                            prev.filter((res) => res.name !== template.name)
                                                        );
                                                    } else {
                                                        // Add or update the pool template with the chosen poolNumber,
                                                        // preserving any existing queueSize (or defaulting to 0).
                                                        setSelectedPools((prev) => {
                                                            const existingIndex = prev.findIndex(
                                                                (res) => res.name === template.name
                                                            );
                                                            const updatedTemplate = {
                                                                ...template,
                                                                poolNumber: newNumber,
                                                                queueSize: existing?.queueSize ?? 0,
                                                            };
                                                            if (existingIndex === -1) {
                                                                return [...prev, updatedTemplate];
                                                            } else {
                                                                const newArray = [...prev];
                                                                newArray[existingIndex] = updatedTemplate;
                                                                return newArray;
                                                            }
                                                        });
                                                    }
                                                }}
                                            >
                                                {[0, 1, 2, 3, 4, 5].map((num) => (
                                                    <option key={num} value={num}>
                                                        {num}
                                                    </option>
                                                ))}
                                            </select>
                                        </label>
                                        <label style={{ marginLeft: "1rem" }}>
                                            Queue Size:{" "}
                                            <select
                                                value={currentQueueSize}
                                                onChange={(e) => {
                                                    const newQueueSize = parseInt(e.target.value, 10);
                                                    setSelectedPools((prev) => {
                                                        const existingIndex = prev.findIndex(
                                                            (res) => res.name === template.name
                                                        );
                                                        if (existingIndex === -1) {
                                                            // If the template isn't already selected, add it with a default poolNumber (e.g., 1)
                                                            // and the chosen queueSize.
                                                            return [
                                                                ...prev,
                                                                { ...template, poolNumber: 1, queueSize: newQueueSize },
                                                            ];
                                                        } else {
                                                            // Otherwise, update the queueSize on the existing entry.
                                                            const newArray = [...prev];
                                                            newArray[existingIndex] = {
                                                                ...newArray[existingIndex],
                                                                queueSize: newQueueSize,
                                                            };
                                                            return newArray;
                                                        }
                                                    });
                                                }}
                                            >
                                                {[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((num) => (
                                                    <option key={num} value={num}>
                                                        {num}
                                                    </option>
                                                ))}
                                            </select>
                                        </label>
                                    </li>
                                );
                            })}
                        </ul>
                    )}
                </div>



                {/* ========== SUBMIT EVENT BUTTON ========== */}
                <button type="submit" style={{marginBottom: "2rem"}}>
                    Create Event
                </button>
            </form>
            <form onSubmit={handleSubmitPool}>
                {/* ========== CREATE Patient Pool Template========== */}
                <div style={{marginBottom: "1rem"}}>
                    <h3>Create Patient Pool Template:</h3>
                    <div style={{marginBottom: "1rem"}}>
                        <label htmlFor="poolName">Name: </label>
                        <input
                            id="poolName"
                            type="text"
                            value={poolName}
                            onChange={(e) => setPoolName(e.target.value)}
                            required
                        />
                    </div>
                    <label>
                        <input
                            type="radio"
                            name="pools"
                            value={PoolType.Bay}
                            checked={poolType === PoolType.Bay}
                            onChange={() => setPoolType(PoolType.Bay)}
                        />
                        Bay
                    </label>

                    <label>
                        <input
                            type="radio"
                            name="pools"
                            value={PoolType.MedService}
                            checked={poolType === PoolType.MedService}
                            onChange={() => setPoolType(PoolType.MedService)}
                        />
                        Medical Service

                    </label>

                    <label>
                        <input
                            type="radio"
                            name="pools"
                            value={PoolType.Floor}
                            checked={poolType === PoolType.Floor}
                            onChange={() => setPoolType(PoolType.Floor)}
                        />
                        Floor
                    </label>

                    {poolType === PoolType.MedService && (
                        <div style={{marginBottom: "1rem"}}>
                            <label htmlFor="patientProcessTime">Patient Process Time: </label>
                            <input
                                id="patientProcessTime"
                                type="text"
                                value={patientProcessTime}
                                onChange={(e) => setPatientProcessTime(e.target.value)}
                                required
                            />minutes
                        </div>)}

                </div>
                <button type="submit" style={{marginBottom: "2rem"}}>
                    Create Pool
                </button>
            </form>
            <UploadImagePanel/>

            <Footer/>
        </div>
    );
}
