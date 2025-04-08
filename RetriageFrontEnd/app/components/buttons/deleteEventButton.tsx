'use client'

export default function DeleteEventButton({id, deleteHandler}: { id: number, deleteHandler: (id: number) => void }) {

    function clickHandle() {
        deleteHandler(id)
    }

    return (
        <main>
            <button className={"btn btn-outline-primary"} onClick={clickHandle}>Delete</button>
        </main>
    )
}