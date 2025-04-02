import React, {MouseEvent, useEffect, useRef, useState} from "react";
import {Patient} from "@/app/models/patient";
import {useDraggable} from "@dnd-kit/core";
import {Condition} from "@/app/enumerations/condition";
import {sendEvent} from "@/app/api/eventWebSocket";
import {GetCookies} from "@/app/api/cookieApi";
import {Event} from "@/app/models/event";
import {Role} from "@/app/enumerations/role";

// Define a mapping from condition to an image URL.
const conditionIcons: Record<Condition, string> = {
    [Condition.Immediate]: "/images/immediate.png",
    [Condition.Delayed]: "/images/delayed.png",
    [Condition.Minor]: "/images/minor.png",
    [Condition.Deceased]: "/images/deceased.png",
};

export default function PatientIcon({
                                        patient,
                                        patientList,
                                        getActiveEvent,
                                    }: {
    patient: Patient;
    patientList: Patient[];
    getActiveEvent: () => Event;
}) {
    const {attributes, listeners, setNodeRef, transform} = useDraggable({
        id: patient.id || 0,
    });
    const style = transform
        ? {
            transform: `translate3d(${transform.x}px, ${transform.y}px, 0)`,
            zIndex: 9999
        }
        : undefined;

    // State for context menu
    const [menuVisible, setMenuVisible] = useState(false);
    const [menuPosition, setMenuPosition] = useState({x: 0, y: 0});
    const [showConditionOptions, setShowConditionOptions] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Ref for the context menu container
    const menuRef = useRef<HTMLDivElement>(null);

    // Right-click handler to show our custom dropdown
    const handleContextMenu = (e: MouseEvent<HTMLDivElement>) => {
        e.preventDefault();
        setMenuVisible(true);
        // Use fixed positioning so that the menu is positioned relative to the viewport.
        setMenuPosition({x: e.clientX, y: e.clientY});
    };

    // Global click handler to close the menu when clicking outside
    useEffect(() => {
        function handleDocumentClick(this: Document, ev: globalThis.MouseEvent) {
            if (menuRef.current && !menuRef.current.contains(ev.target as Node)) {
                setMenuVisible(false);
                setShowConditionOptions(false);
            }
        }

        if (menuVisible) {
            document.addEventListener("mousedown", handleDocumentClick);
        }
        return () => {
            document.removeEventListener("mousedown", handleDocumentClick);
        };
    }, [menuVisible]);

    // Action handlers
    const handleDelete = (e: React.MouseEvent) => {
        e.stopPropagation();
        const index: number = patientList.findIndex(
            (storedPatient) => storedPatient.id === patient.id
        );
        if (index !== -1) {
            patientList.splice(index, 1);
            sendEvent(getActiveEvent());
            console.log("Patient " + patient.id + " deleted");
            setMenuVisible(false);
        } else {
            setError("Patient not found");
        }
    };

    const handleDischarge = (e: React.MouseEvent) => {
        e.stopPropagation();
        const index: number = patientList.findIndex(
            (storedPatient) => storedPatient.id === patient.id
        );
        if (index !== -1) {
            patientList.splice(index, 1);
            sendEvent(getActiveEvent());
            console.log("Patient " + patient.id + " discharged");
            setMenuVisible(false);
        } else {
            setError("Patient not found");
        }
    };

    const handleUpdateCondition = (e: React.MouseEvent, newCondition: Condition) => {
        e.stopPropagation();
        patient.condition = newCondition;
        sendEvent(getActiveEvent());
        setMenuVisible(false);
        setShowConditionOptions(false);
    };

    const toggleConditionOptions = (e: React.MouseEvent) => {
        e.stopPropagation();
        setShowConditionOptions(!showConditionOptions);
    };

    // Render the context menu with fixed positioning
    const renderContextMenu = () => (
        <div
            ref={menuRef}
            style={{
                position: "fixed",
                top: menuPosition.y,
                left: menuPosition.x,
                background: "white",
                border: "1px solid #ccc",
                zIndex: 1000,
                minWidth: "150px",
                boxShadow: "0 2px 8px rgba(0,0,0,0.15)",
            }}
        >
            <div
                className="dropdown-item"
                onClick={handleDelete}
                style={{padding: "8px", cursor: "pointer"}}
            >
                Delete
            </div>
            <div
                className="dropdown-item"
                onClick={handleDischarge}
                style={{padding: "8px", cursor: "pointer"}}
            >
                Discharge
            </div>
            {GetCookies("role") === Role.Director && (
                <>
                    <div
                        className="dropdown-item"
                        onClick={toggleConditionOptions}
                        style={{padding: "8px", cursor: "pointer"}}
                    >
                        Update Condition
                    </div>
                    {showConditionOptions && (
                        <div style={{borderTop: "1px solid #ccc"}}>
                            {Object.values(Condition).map((cond) => (
                                <div
                                    key={cond}
                                    className="dropdown-item"
                                    onClick={(e) => handleUpdateCondition(e, cond)}
                                    style={{padding: "8px", cursor: "pointer"}}
                                >
                                    {cond}
                                </div>
                            ))}
                        </div>
                    )}
                </>
            )}
        </div>
    );

    return (
        <div
            className="border"
            ref={setNodeRef}
            style={style}
            // Disable drag listeners when the context menu is visible.
            {...(menuVisible ? {} : listeners)}
            {...attributes}
            onContextMenu={handleContextMenu}
        >
            {/* Top section with image icon based on condition */}
            <div className="bg-dark" style={{padding: "10%"}}>
                <img
                    src={conditionIcons[patient.condition] || "/images/default.png"}
                    alt={patient.condition}
                    style={{
                        width: "80%",
                        height: "auto",
                        display: "block",
                        margin: "0 auto",
                        borderRadius: "50%",
                    }}
                />
            </div>

            {/* Bottom section with patient card ID */}
            <div style={{padding: "5%"}}>
                {error && <p>{error}</p>}
                <p className="mb-0">{patient.cardId}</p>
            </div>

            {menuVisible && renderContextMenu()}
        </div>
    );
}
