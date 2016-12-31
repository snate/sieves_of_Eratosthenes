defmodule Soe do
  use Application

  def start(_type, _args) do
    import Supervisor.Spec, warn: false

    # Define workers and child supervisors to be supervised
    children = [
      worker(Soe.SieveCreator, []),
      worker(Soe.Receiver, []),
    ]

    opts = [strategy: :one_for_one, name: Soe.Supervisor]
    Supervisor.start_link(children, opts)
  end
end
