"use client";
import React, {useState} from "react";
import {Event} from "@/app/models/event";
import {Patient} from "@/app/models/patient";
import {Condition} from "@/app/enumerations/condition";
import {PoolType} from "@/app/enumerations/poolType";
import {PatientPool} from "@/app/models/patientPool";
import {sendEvent} from "@/app/api/eventWebSocket";


/**
 * We'll define a temporary type that extends Event to include a "patients" field.
 * If your actual Event interface has "patients" already, then you don't need this.
 */


export default function CreatePatient({getActiveEvent}: { getActiveEvent: () => Event }) {
    const [showModal, setShowModal] = useState(false);
    // Form fields
    const [error, setError] = useState<string | null>(null);
    const [cardId, setCardId] = useState(0);
    const [condition, setCondition] = useState<Condition>(Condition.Minor);
    const [selectedBayPoolId, setSelectedBayPoolId] = useState<number | null>(null);
    // Filter out Bay-type pools
    const bayPools = (getActiveEvent().pools || []).filter((p) => p.poolType === PoolType.Bay);

    async function handleAddPatient(e: React.FormEvent) {
        e.preventDefault();

        // 1) Construct new Patient with nurse=null (instead of {} as any)
        const newPatient: Patient = {
            processed: false,
            cardId,
            condition
        };

        // 2) If user selected a Bay pool, attach it
        if (selectedBayPoolId) {
            console.log("Selected pool is ID of: " + selectedBayPoolId);
            const chosenPool = getActiveEvent().pools.find(pool => pool.id === selectedBayPoolId);
            if (chosenPool) {
                if (chosenPool.patients == null) {
                    const newPatientArray: Patient[] = new Array(1);
                    newPatientArray[0] = newPatient;
                    chosenPool.patients = newPatientArray;
                } else {
                    chosenPool.patients.push(newPatient);
                    console.log("Chosen Pool: " + chosenPool.name);
                }
            } else {
                setError("Selected Patient Pool not found.")
            }
        }


        // 4) Persist via the parent-provided function
        try {
            sendEvent(getActiveEvent());
            console.log("Patient added to Patient Pool");
        } catch (err) {
            setError("Failed to add patient");
            console.error("Failed to update event with new patient:", err);
        }

        // 5) Close modal + reset fields
        setShowModal(false);
        setCardId(0);
        setCondition(Condition.Immediate);
        setSelectedBayPoolId(null);
    }

    return (
        <div>
            <button className="btn btn-primary" onClick={() => setShowModal(true)}>Add Patient</button>

            {showModal && (
                <div style={styles.overlay}>
                    <div style={styles.modal}>
                        <h2>Add a New Patient</h2>
                        <form onSubmit={handleAddPatient}>
                            <div>
                                <label>Card ID: </label>
                                <input
                                    type="number"
                                    value={cardId}
                                    onChange={(e) => setCardId(parseInt(e.target.value))}
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
                                    <option value={Condition.Deceased}>Deceased</option>
                                </select>
                            </div>

                            <div>
                                <label>Assign to Bay Pool: </label>
                                {error && <p style={{color: "red"}}>{error}</p>}
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

                            <div style={{marginTop: "1rem"}}>
                                <button type="submit">Add Patient</button>
                                <button
                                    type="button"
                                    onClick={() => setShowModal(false)}
                                    style={{marginLeft: "0.5rem"}}
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
