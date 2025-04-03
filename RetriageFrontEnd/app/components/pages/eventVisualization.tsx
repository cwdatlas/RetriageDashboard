import {Event} from '@/app/models/event'
import {PoolType} from "@/app/enumerations/poolType";
import BayPanel from "@/app/components/panel/bayPannel";
import {useState} from "react";
import {DndContext, DragEndEvent, DragOverEvent, DragOverlay, DragStartEvent} from "@dnd-kit/core";
import {Patient} from "@/app/models/patient";
import {sendEvent} from "@/app/api/eventWebSocket";
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
                if (!patient) {
                    setError("Patient not found");
                    return;
                }

                // Prevent moving from a Floor.
                if (originPool.poolType === PoolType.Floor) {
                    setError("Cannot move patients from a floor.");
                    return;
                }

                // Prevent moving a patient that is currently being processed in a MedService.
                if (
                    originPool.poolType === PoolType.MedService &&
                    originPool.patients.length > 0 &&
                    originPool.patients[0].id === patient.id &&
                    !patient.processed
                ) {
                    setError("Cannot move a patient that is currently being processed at a service.");
                    return;
                }
                // Remove patient from origin pool and add to destination pool.
                if (overPool.patients.length === overPool.queueSize) {
                    setError("The maximum number of patients are already assigned.");
                } else {
                    // Remove the patient from the origin pool.
                    const index = originPool.patients.findIndex(storedPatient => storedPatient.id === patient.id);
                    if (index !== -1) {
                        originPool.patients.splice(index, 1);
                        if(originPool.poolType == PoolType.MedService && index == 0)
                            overPool.startedProcessingAt = Date.now();
                    } else {
                        setError("Patient not found");
                        return;
                    }

                    // Mark patient as not processed
                    patient.processed = false;

                    // Add patient to the destination pool.
                    overPool.patients.push(patient);

                    // For MedService pools, if after adding the patient becomes the first one and is not processed,
                    // update the destination pool's startedProcessingAt.
                    if (overPool.poolType === PoolType.MedService &&
                        overPool.patients.length > 0 &&
                        overPool.patients[0].id === patient.id &&
                        !patient.processed) {
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
