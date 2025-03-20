"use client";
import React, { useState } from "react";
import { Event } from "@/app/models/event";
import { Patient } from "@/app/models/patient";
import { Condition } from "@/app/enumerations/condition";
import { PoolType } from "@/app/enumerations/poolType";
import { PatientPool } from "@/app/models/patientPool";
import {User} from "@/app/models/user";
import {getCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role";
import {updateEvent} from "@/app/api/eventApi";
import SelectEvent from "@/app/components/selectEvent";

/**
 * We'll define a temporary type that extends Event to include a "patients" field.
 * If your actual Event interface has "patients" already, then you don't need this.
 */


export default function CreatePatient({event} : { event : Event }) {
    const [showModal, setShowModal] = useState(false);

    // Form fields
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [condition, setCondition] = useState<Condition>(Condition.Minor);
    const [selectedBayPoolId, setSelectedBayPoolId] = useState<number | null>(null);

    // Filter out Bay-type pools
    const bayPools = (event.pools || []).filter((p) => p.poolType === PoolType.Bay);

    async function handleAddPatient(e: React.FormEvent) {
        e.preventDefault();

        const currentUser: User = {
            email: getCookies("email"),
            firstName: getCookies("firstName"),
            lastName: getCookies("lastName"),
            role: getCookies("role") as Role

        }
        // 1) Construct new Patient with nurse=null (instead of {} as any)
        const newPatient: Patient = {
            firstName,
            lastName,
            condition,
            nurse: currentUser,   // if your Patient interface expects `User | null`
        };

        // 2) If user selected a Bay pool, attach it
        if (selectedBayPoolId) {
            const chosenPool = event.pools.at(0);
            if (chosenPool) {
                chosenPool.patientQueue.push(newPatient);
            }
        }



        // 4) Persist via the parent-provided function
        try {
            await updateEvent(event);
        } catch (err) {
            console.error("Failed to update event with new patient:", err);
        }

        // 5) Close modal + reset fields
        setShowModal(false);
        setFirstName("");
        setLastName("");
        setCondition(Condition.Immediate);
        setSelectedBayPoolId(null);
    }

    return (
        <div>
            <button onClick={() => setShowModal(true)}>Add Patient</button>

            {showModal && (
                <div style={styles.overlay}>
                    <div style={styles.modal}>
                        <h2>Add a New Patient</h2>
                        <form onSubmit={handleAddPatient}>
                            <div>
                                <label>First Name: </label>
                                <input
                                    type="text"
                                    value={firstName}
                                    onChange={(e) => setFirstName(e.target.value)}
                                    required
                                />
                            </div>

                            <div>
                                <label>Last Name: </label>
                                <input
                                    type="text"
                                    value={lastName}
                                    onChange={(e) => setLastName(e.target.value)}
                                    required
                                />
                            </div>

                            <div>
                                <label>Condition: </label>
                                <select
                                    value={condition}
                                    onChange={(e) => setCondition(e.target.value as Condition)}
                                >
                                    <option value={Condition.Immediate}>Immediate</option>
                                    <option value={Condition.Delayed}>Delayed</option>
                                    <option value={Condition.Minor}>Minor</option>
                                    <option value={Condition.Expectant}>Expectant</option>
                                </select>
                            </div>

                            <div>
                                <label>Assign to Bay Pool: </label>
                                <select
                                    value={selectedBayPoolId ?? ""}
                                    onChange={(e) => setSelectedBayPoolId(Number(e.target.value))}
                                >
                                    <option value="">-- Select a Bay Pool --</option>
                                    {bayPools.map((pool: PatientPool) => (
                                        <option key={pool.id} value={pool.id}>
                                            {pool.name}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div style={{ marginTop: "1rem" }}>
                                <button type="submit">Add Patient</button>
                                <button
                                    type="button"
                                    onClick={() => setShowModal(false)}
                                    style={{ marginLeft: "0.5rem" }}
                                >
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

/** Minimal inline modal styles */
const styles = {
    overlay: {
        position: "fixed" as const,
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: "rgba(0,0,0,0.4)",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        zIndex: 9999,
    },
    modal: {
        background: "#fff",
        padding: "1rem",
        borderRadius: "8px",
        minWidth: "300px",
    },
};
