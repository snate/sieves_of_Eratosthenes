defmodule SoeClientComputerTest do
  use ExUnit.Case
  doctest SoeClient.Computer

  defmodule BackendMock do
    use GenServer

    def start_link do
      GenServer.start_link(__MODULE__, -1, name: :ReceiverMock)
    end

    def get_final_value do
      GenServer.call :ReceiverMock, :get
    end

    def handle_call({:next_number, {_id, new}}, _from, old) do
      assert new > old
      {:reply, :mock_reply, new}
    end

    def handle_call(:get, _from, n) do
      {:reply, n, n}
    end
  end

  defmodule EndpointMock do
    use GenServer

    def start_link do
      GenServer.start_link(__MODULE__, -1, name: :ServerMock)
    end

    def get_final_value do
      GenServer.call :ServerMock, :get
    end

    def handle_call({:ask_for, {new, _me}}, _from, old) do
      # it actually receives all the sequential calls from 2 to n
      assert new > old
      {:reply, :mock_reply, new}
    end

    def handle_call(:get, _from, n) do
      {:reply, n, n}
    end
  end

  setup_all do
    BackendMock.start_link
    EndpointMock.start_link
    :ok
  end

  test "actually sends in sequential order" do
    SoeClient.Computer.compute_up_to 4
    final_recv = BackendMock.get_final_value
    assert final_recv > 0
    final_endp = EndpointMock.get_final_value
    assert final_endp > 0
  end
end
