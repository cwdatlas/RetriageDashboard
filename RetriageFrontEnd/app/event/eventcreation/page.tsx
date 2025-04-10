"use client";

import React, {useEffect, useState} from "react";
import {useRouter} from "next/navigation";

import Header from "@/app/components/panel/header";
import Footer from "@/app/components/panel/footer";
import {createPoolTemplate, deletePoolTemplate, getAllPoolTemplates,} from "@/app/api/patientPoolTmpApi";
import {createEvent} from "@/app/api/eventApi";
import {User} from "@/app/models/user";
import {GetCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role";
import {PoolType} from "@/app/enumerations/poolType";
import {PatientPoolTmp} from "@/app/models/patientPoolTmp";
import {EventTmp} from "@/app/models/eventTmp";
import ErrorMessage from "@/app/components/modals/errorMessage";
import ImageSelector from "@/app/components/panel/imageSelector";
import StatusMessage from "@/app/components/modals/statusMessage";
import DeletePoolTmpButton from "@/app/components/buttons/deletePoolTmpButton";

export default function EventCreation() {
    const router = useRouter();

    // Main Event Form Fields
    const [name, setName] = useState("");
    const [duration, setDuration] = useState("");
    // Initialize with a default image so that if the user does not change it, a valid image is passed.
    const [icon, setIcon] = useState("nurse.png");
    const [error, setError] = useState<string | null>(null);
    const [status, setStatus] = useState("");

    // PatientPool Saving Handles
    const [poolName, setPoolName] = useState("");
    const [poolType, setPoolType] = useState(PoolType.MedService);
    const [patientProcessTime, setPatientProcessTime] = useState("");
    const [autoDischarge, setAutoDischarge] = useState(true);

    // Director + Pools for this new event
    const director: User = {
        firstName: GetCookies("firstName"),
        lastName: GetCookies("lastName"),
        email: GetCookies("email"),
        role: GetCookies("role") as Role,
    };

    // State to hold all pool templates from your API
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

    // If switching away from MedService, reset autoDischarge
    useEffect(() => {
        if (poolType !== PoolType.MedService) {
            setAutoDischarge(false);
        }
    }, [poolType]);

    /**
     *  Create the event by selecting the pools
     */
    async function handleSubmitEvent(e: React.FormEvent) {
        e.preventDefault();
        if (!director) {
            setError("Director not loaded yet. Please wait or refresh.");
            return;
        }
        // Basic input validation for event name & duration:
        // Example of preventing event name from containing suspicious characters
        if (/['";]/.test(name)) {
            setError("Event name contains invalid characters.");
            return;
        }

        const parsedDuration = parseInt(duration, 10);
        if (parsedDuration > 200) {
            setError("Duration cannot exceed 200 minutes.");
            return;
        }

        const newEvent: EventTmp = {
            name: name,
            poolTmps: selectedPools,
            duration: parsedDuration * 60000,
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

    /**
     *  Create a new pool template
     */
    async function handleSubmitPool(e: React.FormEvent) {
        e.preventDefault();

        // For Medical Service pools, enforce a unique name and that an icon is selected.
        if (poolType === PoolType.MedService) {
            const duplicate = allTemplates.find(
                (t) =>
                    t.poolType === PoolType.MedService &&
                    t.name.toLowerCase() === poolName.toLowerCase()
            );
            if (duplicate) {
                setError("A medical service pool with this name already exists.");
                return;
            }
            if (!icon) {
                setError("Please select an icon for the Medical Service pool.");
                return;
            }
        }

        const newPool: PatientPoolTmp = {
            queueSize: 1,
            poolType: poolType,
            processTime: parseInt(patientProcessTime) * 60000 || 60000,
            autoDischarge: poolType === PoolType.MedService ? autoDischarge : false,
            name: poolName,
            poolNumber: 1,
            icon: (poolType === PoolType.MedService || poolType === PoolType.Floor) ? icon : "",
        };

        try {
            await createPoolTemplate(newPool);
            setStatus("Successfully created " + newPool.name);
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message || "An error occurred");
            } else {
                setError("An unknown error occurred");
            }
        }

        // Refresh the template list
        const data = await getAllPoolTemplates();
        setAllTemplates(data);
    }

    /**
     * Delete a template
     */
    async function deleteHandler(id: number) {
        setError(null);
        try {
            await deletePoolTemplate(id, setError);
            setAllTemplates((prevTemplates) => prevTemplates.filter((pool) => pool.id !== id));
        } catch (err) {
            if (err instanceof Error) {
                setError(err.message);
            } else {
                setError("An unknown error occurred");
            }
        }
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
                        <StatusMessage statusMessage={status} />
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
                                    // Or do any extra validation check here
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
                            // Here we switch from <ul> to a Bootstrap table
                            <div className="table-responsive">
                                <table className="table table-striped align-middle">
                                    <thead>
                                    <tr>
                                        <th scope="col">Name</th>
                                        <th scope="col">Type</th>
                                        <th scope="col">Pool #<br />(max 5)</th>
                                        <th scope="col">Queue Size<br />(max 100)</th>
                                        <th scope="col">Icon</th>
                                        <th scope="col">Action</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {allTemplates.map((template, idx) => {
                                        const existing = selectedPools.find(
                                            (res) => res.name === template.name
                                        );
                                        const currentPoolNumber = existing?.poolNumber ?? 0;
                                        const currentQueueSize = existing?.queueSize ?? 0;

                                        return (
                                            <tr key={`${template.name}-${idx}`}>
                                                <td>
                                                    <strong>{template.name}</strong>
                                                </td>
                                                <td>{template.poolType}</td>
                                                <td style={{ maxWidth: "90px" }}>
                                                    <input
                                                        type="number"
                                                        className="form-control form-control-sm"
                                                        value={currentPoolNumber}
                                                        onChange={(e) => {
                                                            const newNumber = parseInt(e.target.value, 10);
                                                            if (newNumber < 0 || newNumber > 5) {
                                                                setError("Pool number must be between 0 and 5.");
                                                                return;
                                                            }
                                                            setSelectedPools((prev) => {
                                                                const existingIndex = prev.findIndex(
                                                                    (res) => res.name === template.name
                                                                );
                                                                const updatedTemplate = {
                                                                    ...template,
                                                                    poolNumber: newNumber,
                                                                    queueSize: existing?.queueSize ?? 0,
                                                                    icon: template.icon, // keep the saved icon value
                                                                };
                                                                if (existingIndex === -1) {
                                                                    return [...prev, updatedTemplate];
                                                                } else {
                                                                    const newArray = [...prev];
                                                                    newArray[existingIndex] = updatedTemplate;
                                                                    return newArray;
                                                                }
                                                            });
                                                        }}
                                                    />
                                                </td>
                                                <td style={{ maxWidth: "100px" }}>
                                                    <input
                                                        type="number"
                                                        className="form-control form-control-sm"
                                                        value={currentQueueSize}
                                                        onChange={(e) => {
                                                            const newQueueSize = parseInt(e.target.value, 10);
                                                            if (newQueueSize < 0 || newQueueSize > 100) {
                                                                setError("Queue size must be between 0 and 100.");
                                                                return;
                                                            }
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
                                                    />
                                                </td>
                                                <td>
                                                    {/* Display the pool's icon if it's MedService or floor */}
                                                    {(template.poolType === PoolType.MedService
                                                    || template.poolType == PoolType.Floor && template.icon) ? (
                                                        <img
                                                            src={`/images/${template.icon}`}
                                                            alt={template.name}
                                                            style={{
                                                                width: "40px",
                                                                height: "40px",
                                                                objectFit: "contain",
                                                            }}
                                                        />
                                                    ) : (
                                                        <span className="text-muted">N/A</span>
                                                    )}
                                                </td>
                                                <td>
                                                    {template.id && (
                                                        <DeletePoolTmpButton
                                                            id={template.id}
                                                            deletePoolHandler={deleteHandler}
                                                        />
                                                    )}
                                                </td>
                                            </tr>
                                        );
                                    })}
                                    </tbody>
                                </table>
                            </div>
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
                            {poolType === PoolType.MedService&& (
                                <>
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
                                    <div className="form-check mb-3">
                                        <input
                                            className="form-check-input"
                                            type="checkbox"
                                            id="autoDischarge"
                                            checked={autoDischarge}
                                            onChange={(e) => setAutoDischarge(e.target.checked)}
                                        />
                                        <label className="form-check-label" htmlFor="autoDischarge">
                                            Auto Discharge
                                        </label>
                                    </div>
                                    </>
                                    )}
                            {(poolType === PoolType.MedService || poolType === PoolType.Floor) && (
                                <>
                                    <ImageSelector icon={icon} setIcon={setIcon} />
                                </>
                    )}
                            <button type="submit" className="btn btn-primary">
                                Create Pool
                            </button>
                        </form>
                    </div>
                </div>
            </main>
            <Footer />
        </div>
    );
}
