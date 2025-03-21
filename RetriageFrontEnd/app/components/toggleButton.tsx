"use client";
import React from "react";

interface ToggleButtonProps {
    onToggle: () => void;
    label: string;
}

export default function ToggleButton({ onToggle, label }: ToggleButtonProps) {
    return (
        <button onClick={onToggle}>
            {label}
        </button>
    );
}