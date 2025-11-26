export type Item = {
  id: string;
  owner: string;
  name: string;
  description: string | null;
  active: boolean;
};

export function castToItems(raw: any[]): Item[] {
  return raw.map((obj) => ({
    id: String(obj.id ?? ""),
    owner: String(obj.owner ?? ""),
    name: String(obj.name ?? "Unnamed"),
    description: obj.description ?? null,
    active: Boolean(obj.active),
  }));
}