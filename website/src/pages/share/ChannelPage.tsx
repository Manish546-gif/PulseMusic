import { useParams } from "react-router-dom";
import { UserRound } from "lucide-react";
import { ShareShell } from "./ShareShell";
import { SHARE_URL } from "../../lib/links";

export function ChannelPage() {
  const { id = "" } = useParams();
  const url = `${SHARE_URL}/channel/${encodeURIComponent(id)}`;

  return (
    <ShareShell
      kind="Shared via Pulse Music"
      headline="An artist was shared with you"
      description="Open their profile in Pulse Music to explore the full catalog, top tracks, and albums."
      url={url}
      icon={UserRound}
      meta={url}
    />
  );
}