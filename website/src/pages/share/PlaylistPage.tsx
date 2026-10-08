import { useSearchParams } from "react-router-dom";
import { ListMusic } from "lucide-react";
import { ShareShell } from "./ShareShell";
import { SHARE_URL } from "../../lib/links";

export function PlaylistPage() {
  const [params] = useSearchParams();
  const listId = params.get("list") ?? "";
  const url = `${SHARE_URL}/playlist?list=${encodeURIComponent(listId)}`;

  return (
    <ShareShell
      kind="Shared via Pulse Music"
      headline="A playlist was shared with you"
      description="Open it in Pulse Music to play, shuffle, or download the whole playlist for offline listening."
      url={url}
      icon={ListMusic}
      meta={url}
    />
  );
}