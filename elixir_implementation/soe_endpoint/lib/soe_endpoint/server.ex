defmodule SoeEndpoint.Server do
  use GenServer

  @doc """
  Starts the SoE's server.
  """
  def start_link do
    GenServer.start_link(__MODULE__, %{}, name: :Server)
  end

  # SERVER CALLBACKS
  def handle_call({:ask_for, {num, asker}}, _from, requests) do
    requests_for_num = Map.get requests, num, []
    new_requests = Map.put requests, num, [asker | requests_for_num]
    {:reply, :registered, new_requests}
  end

  def handle_call({:answer_for, {num, response}}, _from, requests) do
    requests_for_num = Map.get requests, num, []
    give_answer_to requests_for_num, num, response
    new_requests = Map.drop requests, [num]
    {:reply, :ok, new_requests}
  end

  defp give_answer_to([], _num, _response) do
  end

  defp give_answer_to([asker | remainder], num, response) do
    GenServer.cast asker, {:answer, {num, response}}
    give_answer_to remainder, num, response
  end

end
