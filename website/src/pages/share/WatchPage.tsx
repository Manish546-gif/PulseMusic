import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { Play } from "lucide-react";
import { ShareShell } from "./ShareShell";
import { SHARE_URL } from "../../lib/links";
import { getOEmbed, type OEmbedData } from "../../lib/share";

export function WatchPage() {
  const [params] = useSearchParams();
  const videoId = params.get("v") ?? "";
  const [oembed, setOembed] = useState<OEmbedData | undefined>();

  useEffect(() => {
    let active = true;
    void getOEmbed(videoId).then((data) => {
      if (active) setOembed(data);
    });
    return () => {
      active = false;
    };
  }, [videoId]);

  const url = `${SHARE_URL}/watch?v=${encodeURIComponent(videoId)}`;

  return (
    <ShareShell
      kind="Shared via Pulse Music"
      headline={oembed?.title ?? "A song was shared with you"}
      description={
        oembed?.author_name
          ? `Streamed on Pulse Music — brought to you by ${oembed.author_name}.`
          : "Someone shared this track with you. Open it in Pulse Music for ad-free playback, synced lyrics, and offline listening."
      }
      url={url}
      icon={Play}
      art={
        oembed?.thumbnail_url
          ? { src: oembed.thumbnail_url, alt: oembed.author_name ?? "Shared song artwork" }
          : undefined
      }
      meta={url}
    />
  );
}