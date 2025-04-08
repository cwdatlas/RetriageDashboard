'use client'


export default function DeletePoolTmpButton({id, deletePoolHandler}: {
    id: number,
    deletePoolHandler: (id: number) => void
}) {
    function handler() {
        deletePoolHandler(id)
    }

    return (
        <main>
            <button type="button" className={"btn btn-outline-primary"} onClick={handler}>Delete</button>
        </main>
    )
}