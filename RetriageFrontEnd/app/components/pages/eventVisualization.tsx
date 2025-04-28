import {Event} from '@/app/models/event'
import {PoolType} from "@/app/enumerations/poolType";
import BayPanel from "@/app/components/panel/bayPannel";
import React, {useState} from "react";
import {DndContext, DragEndEvent, DragOverEvent, DragOverlay, DragStartEvent} from "@dnd-kit/core";
import {Patient} from "@/app/models/patient";
import {sendEvent} from "@/app/api/eventWebSocket";
import MedServicePanel from "@/app/components/panel/medServicePanel";
import FloorPanel from "@/app/components/panel/floorPannel";
import PatientIcon from "@/app/components/panel/patientIcon";
import ErrorMessage from "@/app/components/modals/errorMessage";

export default function EventVisualization({getActiveEvent}: { getActiveEvent: () => Event }) {
    const event = getActiveEvent();
    const [originPoolId, setOriginPoolIdId] = useState<number | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [activePatient, setActivePatient] = useState<Patient | null>(null);

    function handleDragStart(event: DragStartEvent) {
        console.log("start", event.active.id);
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
                const patient = originPool.patients.find(p => p.id === event.active.id);
                if (!patient) {
                    setError("Patient not found");
                    return;
                }
                if (originPool.poolType === PoolType.Floor) {
                    setError("Cannot move patients from a floor.");
                    return;
                }
                if (
                    originPool.poolType === PoolType.MedService &&
                    originPool.patients.length > 0 &&
                    originPool.patients[0].id === patient.id &&
                    !patient.processed
                ) {
                    setError("Cannot move a patient that is currently being processed at a service.");
                    return;
                }
                if (overPool.patients.length === overPool.queueSize) {
                    setError("The maximum number of patients are already assigned.");
                } else {
                    const index = originPool.patients.findIndex(sp => sp.id === patient.id);
                    if (index !== -1) {
                        originPool.patients.splice(index, 1);
                        if (originPool.poolType === PoolType.MedService && index === 0) {
                            overPool.startedProcessingAt = Date.now();
                        }
                    } else {
                        setError("Patient not found");
                        return;
                    }
                    patient.processed = false;
                    if (originPool.poolType === PoolType.MedService && index === 0 && originPool.patients.length > 0) {
                        originPool.startedProcessingAt = Date.now();
                    }
                    overPool.patients.push(patient);
                    if (
                        overPool.poolType === PoolType.MedService &&
                        overPool.patients.length > 0 &&
                        overPool.patients[0].id === patient.id &&
                        !patient.processed
                    ) {
                        overPool.startedProcessingAt = Date.now();
                    }
                    sendEvent(getActiveEvent());
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
            const id = typeof event.over.id === "string" ? parseInt(event.over.id) : event.over.id;
            setOriginPoolIdId(id);
        }
    }

    return (
        <DndContext onDragEnd={handleDragEnd} onDragStart={handleDragStart} onDragOver={handleDragOver}>
            <h2>{event.name}</h2>
            <ErrorMessage errorMessage={error}/>
            <div className="container-fluid">
                <div className="row">
                    {/* First Column: Bay Panels */}
                    <div className="col-4">
                        {event.pools.map((template, idx) =>
                            template.poolType === PoolType.Bay ? (
                                <div key={idx} className="mb-3">
                                    <BayPanel bay={template} getActiveEvent={getActiveEvent}/>
                                </div>
                            ) : null
                        )}
                    </div>
                    {/* Second Column: MedService Panels */}
                    <div className="col-8">
                        <div className="row row-cols-4 g-2">
                            {event.pools.map((template, idx) =>
                                template.poolType === PoolType.MedService ? (
                                    <div key={idx} className="col">
                                        <MedServicePanel service={template} getActiveEvent={getActiveEvent}/>
                                    </div>
                                ) : null
                            )}
                        </div>
                    </div>
                </div>
                <div className="row content-center mt-4">Floors</div>
                <hr/>
                <div className="row row-cols-3">
                    {event.pools.map((template, idx) =>
                        template.poolType === PoolType.Floor ? (
                            <div key={idx} className="col mb-3">
                                <FloorPanel floor={template} getActiveEvent={getActiveEvent}/>
                            </div>
                        ) : null
                    )}
                </div>
            </div>
            <DragOverlay>
                {activePatient ? (
                    <PatientIcon
                        patient={activePatient}
                        patientList={[]}
                        getActiveEvent={getActiveEvent}
                    />
                ) : null}
            </DragOverlay>
        </DndContext>
    );
}
