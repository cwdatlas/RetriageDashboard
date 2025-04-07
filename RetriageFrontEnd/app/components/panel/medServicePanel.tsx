'use client'

import PatientIcon from "@/app/components/panel/patientIcon";
import {PatientPool} from "@/app/models/patientPool";
import {useDroppable} from "@dnd-kit/core";
import {Event} from "@/app/models/event";
import {PoolType} from "@/app/enumerations/poolType";

export default function MedServicePanel({service, getActiveEvent}: {
    service: PatientPool,
    getActiveEvent: () => Event
}) {
    const {isOver, setNodeRef} = useDroppable({
        id: service.id || 0,
    });
    const style = {
        color: isOver ? 'green' : undefined,
    };

    // Determine if there is a currently processing patient.
    // Here, we assume that if there's a patient at index 0 and they are not yet marked as processed,
    // then that patient is the one being processed.
    const currentlyProcessing =
        service.poolType === PoolType.MedService && service.patients.length > 0
            ? service.patients[0]
            : null;

    return (
        <div ref={setNodeRef} style={style} className="card rounded mb-3">
            <div className="card-header d-flex justify-content-between align-items-center">
                <div>
                    <h2 className="card-title">{service.name}</h2>
                    {currentlyProcessing && !currentlyProcessing.processed && (
                        <small className="text-muted">
                            Processing: {currentlyProcessing.cardId}
                        </small>
                    )}
                </div>
                <div className="d-flex align-items-center">
                    <img
                        src="/images/bed.png"
                        alt="Bed Icon"
                        style={{width: "24px", height: "24px", marginRight: "4px"}}
                    />
                    <span>{service.queueSize - service.patients.length}</span>
                </div>
            </div>
            <div className="card-body">
                <div className="row row-cols-6">
                    {service.patients.map((patient, idx) => (
                        <div key={idx} className="col mb-2">
                            <PatientIcon patient={patient} patientList={service.patients}
                                         getActiveEvent={getActiveEvent}/>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
