import { Event } from '@/app/models/event'
import { PoolType } from "@/app/enumerations/poolType";
import BayPanel from "@/app/components/panel/bayPannel";

export default function EventVisualization({ getActiveEvent }: { getActiveEvent: () => Event }) {
    const event = getActiveEvent();
    return (
        <div>
            <h2>{event.name}</h2>
            <div className="row">
                {/* First Column: Bay Panels */}
                <div className="col">
                    {event.pools.map((template, idx) => {
                        return (
                            template.poolType === PoolType.Bay && (
                                <div key={idx}>
                                    <BayPanel bay={template} />
                                </div>
                            )
                        )
                    })}
                </div>
                {/* Second Column: MedService Panels */}
                <div className="col">
                    {event.pools.map((template, idx) => {
                        return (
                            template.poolType === PoolType.MedService && (
                                <div key={idx}>
                                    <BayPanel bay={template} />
                                </div>
                            )
                        )
                    })}
                </div>
            </div>
        </div>
    )
}
