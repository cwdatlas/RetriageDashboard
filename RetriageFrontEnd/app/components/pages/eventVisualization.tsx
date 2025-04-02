import { Event } from '@/app/models/event'
import { PoolType } from "@/app/enumerations/poolType";
import BayPanel from "@/app/components/panel/bayPannel";
import { useState } from "react";
import { DndContext, DragEndEvent, DragOverEvent, DragStartEvent, DragOverlay } from "@dnd-kit/core";
import { Patient } from "@/app/models/patient";
import { sendEvent } from "@/app/api/eventWebSocket";
import MedServicePanel from "@/app/components/panel/medServicePanel";
import FloorPanel from "@/app/components/panel/floorPannel";
import PatientIcon from "@/app/components/panel/patientIcon";

export default function EventVisualization({ getActiveEvent }: { getActiveEvent: () => Event }) {
    const event = getActiveEvent();
    const [originPoolId, setOriginPoolIdId] = useState<number | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [activePatient, setActivePatient] = useState<Patient | null>(null);

    function handleDragStart(event: DragStartEvent) {
        console.log("start", event.active.id);
        // Find the patient from all pools matching the active id.
        const active = getActiveEvent().pools
            .flatMap(pool => pool.patients)
            .find(patient => patient.id === event.active.id);
        setActivePatient(active || null);
        setOriginPoolIdId(null);
    }

    function handleDragEnd(event: DragEndEvent) {
        const over = event.over;
        if (over && originPoolId && originPoolId !== over.id) {
            console.log("droppable", event.over);
            console.log("draggable", event.active.id);
            console.log("origin", originPoolId);

            const overPool = getActiveEvent().pools.find(pool => pool.id === over.id);
            const originPool = getActiveEvent().pools.find(pool => pool.id === originPoolId);
            if (overPool && originPool) {
                const patient = originPool.patients.find(patient => patient.id === event.active.id);
                if (originPool.poolType === PoolType.Floor) {
                    setError("Cannot assign patients after they are assigned to a bed on a floor.");
                } else if (overPool.patients.length === overPool.queueSize) {
                    setError("The maximum number of patients are already assigned.");
                } else if (!overPool.patients && patient) {
                    const newPatientArray: Patient[] = [patient];
                    overPool.patients = newPatientArray;
                    const index: number = originPool.patients.findIndex(storedPatient => storedPatient.id === patient.id);
                    if (index !== -1) {
                        originPool.patients.splice(index, 1);
                        sendEvent(getActiveEvent());
                    } else {
                        setError("Patient not found");
                    }
                } else if (patient) {
                    overPool.patients.push(patient);
                    const index: number = originPool.patients.findIndex(storedPatient => storedPatient.id === patient.id);
                    if (index !== -1) {
                        originPool.patients.splice(index, 1);
                        sendEvent(getActiveEvent());
                    } else {
                        setError("Patient not found");
                    }
                }
            } else {
                setError("Selected Patient Pool not found.");
            }
        }
        setActivePatient(null);
    }

    function handleDragOver(event: DragOverEvent) {
        console.log("over", event.active.id);
        if (!originPoolId && event.over) {
            if (typeof event.over.id === "string") {
                setOriginPoolIdId(parseInt(event.over.id));
            } else {
                setOriginPoolIdId(event.over.id);
            }
        }
    }

    return (
        <DndContext onDragEnd={handleDragEnd} onDragStart={handleDragStart} onDragOver={handleDragOver}>
            <h2>{event.name}</h2>
            {error && <p>{error}</p>}
            <div className="row">
                {/* First Column: Bay Panels */}
                <div className="col">
                    {event.pools.map((template, idx) =>
                        template.poolType === PoolType.Bay ? (
                            <div key={idx}>
                                <BayPanel bay={template} getActiveEvent={getActiveEvent} />
                            </div>
                        ) : null
                    )}
                </div>
                {/* Second Column: MedService Panels */}
                <div className="col">
                    {event.pools.map((template, idx) =>
                        template.poolType === PoolType.MedService ? (
                            <div key={idx}>
                                <MedServicePanel service={template} getActiveEvent={getActiveEvent} />
                            </div>
                        ) : null
                    )}
                </div>
                {/* Horizontal separator */}
                <div className="row content-center">Floors</div>
                <hr />
                {/* Third Section: Floor Panels in three columns */}
                <div className="row row-cols-3">
                    {event.pools.map((template, idx) =>
                        template.poolType === PoolType.Floor ? (
                            <div key={idx} className="col">
                                <FloorPanel floor={template} getActiveEvent={getActiveEvent} />
                            </div>
                        ) : null
                    )}
                </div>
            </div>
            <DragOverlay>
                {activePatient ? (
                    <PatientIcon
                        patient={activePatient}
                        patientList={[]} // this overlay doesn't need the full list
                        getActiveEvent={getActiveEvent}
                    />
                ) : null}
            </DragOverlay>
        </DndContext>
    );
}
