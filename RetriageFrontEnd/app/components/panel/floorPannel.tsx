'use client'

import { PatientPool } from "@/app/models/patientPool";
import PatientIcon from "@/app/components/panel/patientIcon";
import { useDroppable } from "@dnd-kit/core";
import { Event } from "@/app/models/event";
import React from "react";

export default function FloorPanel({ floor, getActiveEvent }: { floor: PatientPool; getActiveEvent: () => Event }) {
    const { isOver, setNodeRef } = useDroppable({
        id: floor.id || 0,
    });
    const style = {
        color: isOver ? "green" : undefined,
    };

    return (
        <div ref={setNodeRef} style={style} className="card rounded mb-3">
            <div className="card-header d-flex justify-content-between align-items-center">
                <h2 className="card-title mb-0">{floor.name}</h2>
                <div className="d-flex align-items-center">
                    <img
                        src="/images/bed.png"
                        alt="Bed Icon"
                        style={{ width: "24px", height: "24px", marginRight: "4px" }}
                    />
                    <span>{floor.queueSize - floor.patients.length}</span>
                </div>
            </div>
            <div className="card-body">
                <div className="d-flex row row-cols">
                    {/*
            Patient icons in a horizontal scroll if many,
            each in a small container to keep them from blowing up
          */}
                    <div className="d-flex flex-row flex-nowrap overflow-auto" style={{ flex: 1 }}>
                        {floor.patients.map((patient, idx) => (
                            <div key={idx} className="col-sm-4">
                                <PatientIcon
                                    patient={patient}
                                    patientList={floor.patients}
                                    getActiveEvent={getActiveEvent}
                                />
                            </div>
                        ))}
                    </div>

                    {/* Floor icon container - fixed smaller size */}
                    {floor.icon && (
                        <div className="ms-1 col-sm-3">
                            <img
                                src={`/images/${floor.icon}`}
                                alt={floor.name}
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
