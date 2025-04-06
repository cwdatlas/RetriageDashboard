"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

import Header from "@/app/components/panel/header";
import Footer from "@/app/components/panel/footer";
import { createPoolTemplate, getAllPoolTemplates } from "@/app/api/patientPoolTmpApi";
import { createEvent } from "@/app/api/eventApi";
import { User } from "@/app/models/user";
import { GetCookies } from "@/app/api/cookieApi";
import { Role } from "@/app/enumerations/role";
import { PoolType } from "@/app/enumerations/poolType";
import { PatientPoolTmp } from "@/app/models/patientPoolTmp";
import { EventTmp } from "@/app/models/eventTmp";
import UploadImagePanel from "@/app/components/panel/uploadImagePanel";
import ErrorMessage from "@/app/components/modals/errorMessage";

export default function EventCreation() {
    const router = useRouter();

    // Main Event Form Fields
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

    // State to hold *all* pool templates from your API
    const [allTemplates, setAllTemplates] = useState<PatientPoolTmp[]>([]);

    // State for the user-selected Pools (the ones actually going into the event)
    const [selectedPools, setSelectedPools] = useState<PatientPoolTmp[]>([]);

    useEffect(() => {
        async function fetchTemplates() {
            try {
                const data = await getAllPoolTemplates();
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

    async function handleSubmitPool(e: React.FormEvent) {
        e.preventDefault();
        const newPool: PatientPoolTmp = {
            queueSize: 1,
            poolType: poolType,
            processTime: parseInt(patientProcessTime) * 60000 || 60000,
            usable: true,
            name: poolName,
            poolNumber: 1,
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
            <Header />
            <main className="container my-5">
                {/* Event Creation Card */}
                <div className="card shadow-sm mb-4">
                    <div className="card-header">
                        <h2 className="mb-0">Create a New Event</h2>
                    </div>
                    <div className="card-body">
                        <ErrorMessage errorMessage={error} />
                        <form onSubmit={handleSubmitEvent}>
                            <div className="mb-3">
                                <label htmlFor="eventName" className="form-label">
                                    Event Name:
                                </label>
                                <input
                                    id="eventName"
                                    type="text"
                                    className="form-control"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="eventDuration" className="form-label">
                                    Duration (minutes):
                                </label>
                                <input
                                    id="eventDuration"
                                    type="number"
                                    className="form-control"
                                    value={duration}
                                    onChange={(e) => setDuration(e.target.value)}
                                    required
                                />
                            </div>
                            <button type="submit" className="btn btn-primary">
                                Create Event
                            </button>
                        </form>
                    </div>
                </div>

                {/* Pool Templates List Card */}
                <div className="card shadow-sm mb-4">
                    <div className="card-header">
                        <h3 className="mb-0">Pool Templates</h3>
                    </div>
                    <div className="card-body">
                        {allTemplates.length === 0 ? (
                            <p>Loading or none found...</p>
                        ) : (
                            <ul className="list-group">
                                {allTemplates.map((template, idx) => {
                                    const existing = selectedPools.find(
                                        (res) => res.name === template.name
                                    );
                                    const currentPoolNumber = existing?.poolNumber ?? 0;
                                    const currentQueueSize = existing?.queueSize ?? 0;
                                    return (
                                        <li
                                            key={template.name ?? idx}
                                            className="list-group-item d-flex justify-content-between align-items-center"
                                        >
                                            <div>
                                                <div>
                                                    <strong>Name:</strong> {template.name}
                                                </div>
                                                <div>
                                                    <strong>Type:</strong> {template.poolType}
                                                </div>
                                            </div>
                                            <div className="d-flex align-items-center">
                                                <div className="me-2">
                                                    <label className="form-label mb-0 me-1">
                                                        Pool Number:
                                                    </label>
                                                    <select
                                                        value={currentPoolNumber}
                                                        className="form-select form-select-sm"
                                                        onChange={(e) => {
                                                            const newNumber = parseInt(e.target.value, 10);
                                                            if (newNumber === 0) {
                                                                setSelectedPools((prev) =>
                                                                    prev.filter((res) => res.name !== template.name)
                                                                );
                                                            } else {
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
                                                </div>
                                                <div>
                                                    <label className="form-label mb-0 me-1">
                                                        Queue Size:
                                                    </label>
                                                    <select
                                                        value={currentQueueSize}
                                                        className="form-select form-select-sm"
                                                        onChange={(e) => {
                                                            const newQueueSize = parseInt(e.target.value, 10);
                                                            setSelectedPools((prev) => {
                                                                const existingIndex = prev.findIndex(
                                                                    (res) => res.name === template.name
                                                                );
                                                                if (existingIndex === -1) {
                                                                    return [
                                                                        ...prev,
                                                                        {
                                                                            ...template,
                                                                            poolNumber: 1,
                                                                            queueSize: newQueueSize,
                                                                        },
                                                                    ];
                                                                } else {
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
                                                        {[
                                                            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                                                        ].map((num) => (
                                                            <option key={num} value={num}>
                                                                {num}
                                                            </option>
                                                        ))}
                                                    </select>
                                                </div>
                                            </div>
                                        </li>
                                    );
                                })}
                            </ul>
                        )}
                    </div>
                </div>

                {/* Create Patient Pool Template Card */}
                <div className="card shadow-sm mb-4">
                    <div className="card-header">
                        <h3 className="mb-0">Create Patient Pool Template</h3>
                    </div>
                    <div className="card-body">
                        <form onSubmit={handleSubmitPool}>
                            <div className="mb-3">
                                <label htmlFor="poolName" className="form-label">
                                    Pool Name:
                                </label>
                                <input
                                    id="poolName"
                                    type="text"
                                    className="form-control"
                                    value={poolName}
                                    onChange={(e) => setPoolName(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="mb-3">
                                <label className="form-label">Pool Type:</label>
                                <div>
                                    <div className="form-check form-check-inline">
                                        <input
                                            className="form-check-input"
                                            type="radio"
                                            name="pools"
                                            value={PoolType.Bay}
                                            checked={poolType === PoolType.Bay}
                                            onChange={() => setPoolType(PoolType.Bay)}
                                        />
                                        <label className="form-check-label">Bay</label>
                                    </div>
                                    <div className="form-check form-check-inline">
                                        <input
                                            className="form-check-input"
                                            type="radio"
                                            name="pools"
                                            value={PoolType.MedService}
                                            checked={poolType === PoolType.MedService}
                                            onChange={() => setPoolType(PoolType.MedService)}
                                        />
                                        <label className="form-check-label">Medical Service</label>
                                    </div>
                                    <div className="form-check form-check-inline">
                                        <input
                                            className="form-check-input"
                                            type="radio"
                                            name="pools"
                                            value={PoolType.Floor}
                                            checked={poolType === PoolType.Floor}
                                            onChange={() => setPoolType(PoolType.Floor)}
                                        />
                                        <label className="form-check-label">Floor</label>
                                    </div>
                                </div>
                            </div>
                            {poolType === PoolType.MedService && (
                                <div className="mb-3">
                                    <label htmlFor="patientProcessTime" className="form-label">
                                        Patient Process Time (minutes):
                                    </label>
                                    <input
                                        id="patientProcessTime"
                                        type="number"
                                        className="form-control"
                                        value={patientProcessTime}
                                        onChange={(e) => setPatientProcessTime(e.target.value)}
                                        required
                                    />
                                </div>
                            )}
                            <button type="submit" className="btn btn-primary">
                                Create Pool
                            </button>
                        </form>
                    </div>
                </div>

                {/* Upload Image Panel */}
                <div className="mb-4">
                    <UploadImagePanel />
                </div>
            </main>
            <Footer />
        </div>
    );
}
