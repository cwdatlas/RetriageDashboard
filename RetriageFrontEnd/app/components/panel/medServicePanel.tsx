'use client'

import React, { useEffect, useState } from "react";
import PatientIcon from "@/app/components/panel/patientIcon";
import { PatientPool } from "@/app/models/patientPool";
import { useDroppable } from "@dnd-kit/core";
import { Event } from "@/app/models/event";
import { PoolType } from "@/app/enumerations/poolType";

interface ProcessingProgressBarProps {
    processTime: number; // in milliseconds
}

const ProcessingProgressBar: React.FC<ProcessingProgressBarProps> = ({ processTime }) => {
    const [progress, setProgress] = useState(0);

    useEffect(() => {
        const startTime = Date.now();
        const timer = setInterval(() => {
            const elapsed = Date.now() - startTime;
            const newProgress = Math.min((elapsed / processTime) * 100, 100);
            setProgress(newProgress);
            if (newProgress >= 100) clearInterval(timer);
        }, 100);
        return () => clearInterval(timer);
    }, [processTime]);

    return (
        <div
            className="progress"
            style={{
                position: "absolute",
                top: 0,
                left: 0,
                right: 0,
                height: "100%",
                opacity: 0.7,
                zIndex: 10,
            }}
        >
            <div
                className="progress-bar progress-bar-striped progress-bar-animated bg-info"
                role="progressbar"
                style={{ width: `${progress}%` }}
                aria-valuenow={progress}
                aria-valuemin={0}
                aria-valuemax={100}
            ></div>
        </div>
    );
};

export default function MedServicePanel({ service, getActiveEvent }: {
    service: PatientPool,
    getActiveEvent: () => Event
}) {
    const { isOver, setNodeRef } = useDroppable({
        id: service.id || 0,
    });
    const style = {
        color: isOver ? "green" : undefined,
    };

    // We assume that when the poolType is MedService and there is at least one patient,
    // the patient at index 0 (if not marked as processed) is the one currently processing.
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
                        style={{ width: "24px", height: "24px", marginRight: "4px" }}
                    />
                    <span>{service.queueSize - service.patients.length}</span>
                </div>
            </div>
            <div className="card-body">
                <div className="d-flex align-items-center">
                    {/* Patients container – here we render the patients.
              If the patient is currently processing, we add a progress bar overlay. */}
                    <div className="row row-cols">
                        {service.patients.map((patient, idx) => {
                            const isProcessing =
                                currentlyProcessing &&
                                patient.id === currentlyProcessing.id &&
                                !patient.processed;
                            if (isProcessing) {
                                return (
                                    <div key={patient.id || idx} className="col-sm-4 mb-1" style={{ position: "relative" }}>
                                        <PatientIcon
                                            patient={patient}
                                            patientList={service.patients}
                                            getActiveEvent={getActiveEvent}
                                        />
                                        <ProcessingProgressBar processTime={service.processTime} />
                                    </div>
                                );
                            } else {
                                return (
                                    <div key={patient.id || idx} className="col-sm-4 mb-1">
                                        <PatientIcon
                                            patient={patient}
                                            patientList={service.patients}
                                            getActiveEvent={getActiveEvent}
                                        />
                                    </div>
                                );
                            }
                        })}
                    </div>
                    {/* Icon container: fixed size, outlined, aligned to right */}
                    {service.icon && (
                        <div className="ms-auto col-sm-3">
                            <img
                                src={`/images/${service.icon}`}
                                alt={service.name}
                                style={{
                                    maxWidth: "100%",
                                    maxHeight: "100%",
                                    objectFit: "contain",
                                }}
                            />
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
