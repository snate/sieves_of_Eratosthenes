defmodule Soe.SieveCreator do
  use GenServer
  import Supervisor.Spec, warn: false

  @doc """
  Starts the sieve creator.
  """
  def start_link do
    GenServer.start_link(__MODULE__, %{}, name: :SieveCreator)
  end

  @doc """
  Creates a sieve with given `id and `number.
  """
  def create_sieve(id, num) do
    GenServer.call(:SieveCreator, {:new_sieve, {id, num}})
  end

  # SERVER CALLBACKS
  def handle_call({:new_sieve, {id, num}}, _from, ids_map)
    when is_integer(id) and is_integer(num) do
    if Map.has_key?(ids_map, id) do
      {:reply, :already_present, ids_map}
    else
      {:ok, pid} = Supervisor.start_link [worker(Soe.Sieve, [id,num])],
                                         strategy: :one_for_one
      {:reply, pid, Map.put(ids_map,id,num)}
    end
  end

  def handle_call({:new_sieve, _args}, _from, map) do
    {:reply, :bad_argument, map}
  end
end
