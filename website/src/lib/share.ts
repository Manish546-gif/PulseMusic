export interface OEmbedData {
  title?: string;
  author_name?: string;
  author_url?: string;
  thumbnail_url?: string;
  provider_name?: string;
}

const cache = new Map<string, OEmbedData | undefined>();

export async function getOEmbed(videoId: string): Promise<OEmbedData | undefined> {
  if (!videoId) return undefined;
  if (cache.has(videoId)) return cache.get(videoId);
  try {
    const controller = new AbortController();
    const timeout = window.setTimeout(() => controller.abort(), 6000);
    const res = await fetch(
      `https://www.youtube.com/oembed?url=${encodeURIComponent(
        `https://www.youtube.com/watch?v=${videoId}`
      )}&format=json`,
      { signal: controller.signal }
    );
    window.clearTimeout(timeout);
    if (!res.ok) return undefined;
    const data = (await res.json()) as OEmbedData;
    cache.set(videoId, data);
    return data;
  } catch {
    return undefined;
  }
}