export default function Home() {
  const statuses = ["TODO", "DOING", "DONE"] as const;

  return (
    <main className="min-h-screen bg-zinc-50 px-6 py-8 text-zinc-950">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-8">
        <header className="flex flex-col gap-3 border-b border-zinc-200 pb-6 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p className="text-sm font-medium text-zinc-500">Team Tasks Lite</p>
            <h1 className="mt-2 text-3xl font-semibold tracking-normal">
              タスクボード
            </h1>
          </div>
          <button className="h-11 rounded-md bg-zinc-950 px-4 text-sm font-medium text-white transition-colors hover:bg-zinc-800">
            新規タスク
          </button>
        </header>

        <section className="grid gap-4 md:grid-cols-3">
          {statuses.map((status) => (
            <div
              key={status}
              className="min-h-64 rounded-lg border border-zinc-200 bg-white p-4"
            >
              <div className="flex items-center justify-between">
                <h2 className="text-sm font-semibold text-zinc-700">
                  {status}
                </h2>
                <span className="rounded-full bg-zinc-100 px-2 py-1 text-xs font-medium text-zinc-500">
                  0
                </span>
              </div>
              <div className="mt-8 rounded-md border border-dashed border-zinc-200 p-4 text-sm text-zinc-500">
                タスクはありません
              </div>
            </div>
          ))}
        </section>
      </div>
    </main>
  );
}
