'use client'

import {deleteEvent} from "@/app/api/eventApi";
import {useState} from "react";

export default function DeleteEventButton(id: number) {
    const [error, setError] = useState<string | null>(null);

    function deleteHandler() {
        deleteEvent(id, setError)
        setError("button pressed");
    }

    return (
        <main>
            {error && (<div className="alert alert-danger" role="alert">
                    {error}
            </div>
            )}
            <button className={"btn btn-primary"} onClick={deleteHandler}>Delete</button>
        </main>
    )
}