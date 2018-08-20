/*
 * Copyright (c) 2018, NotFoxtrot <http://github.com/NotFoxtrot>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.pyramidplunder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class PyramidPlunderOverlay extends Overlay
{
	private final Client client;
	private final PyramidPlunderConfig config;
	private final PanelComponent panelComponent = new PanelComponent();

	private static final int MAX_TICK_COUNT = 500;
	private static final double TICK_LENGTH = 0.6;

	private static final NumberFormat TIME_LEFT_FORMATTER = DecimalFormat.getInstance(Locale.US);

	static
	{
		((DecimalFormat) TIME_LEFT_FORMATTER).applyPattern("#0.0");
	}

	@Inject
	PyramidPlunderOverlay(Client client, PyramidPlunderConfig config)
	{
		setPosition(OverlayPosition.TOP_LEFT);
		this.client = client;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Widget widget = client.getWidget(WidgetInfo.PYRAMID_PLUNDER_DATA);
		if (widget == null)
		{
			return null;
		}

		toggleDefaultWidget(config.hideWidget());

		panelComponent.getChildren().clear();

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Pyramid Plunder")
			.build());

		//Calculate time based on current pp timer tick
		final int currentTick = client.getVar(Varbits.PYRAMID_PLUNDER_TIMER);
		final double baseTick = (MAX_TICK_COUNT - currentTick) * TICK_LENGTH;
		final double timeLeft = Math.max(0.0, baseTick);
		final String timeLeftStr = TIME_LEFT_FORMATTER.format(timeLeft);

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Time left: ")
			.right(timeLeftStr)
			.rightColor(getColor(currentTick))
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Room:")
			.right(String.valueOf(client.getVar(Varbits.PYRAMID_PLUNDER_ROOM)) + "/8")
			.build());

		return panelComponent.render(graphics);
	}

	void toggleDefaultWidget(boolean hide)
	{
		final Widget widget = client.getWidget(WidgetInfo.PYRAMID_PLUNDER_DATA);

		if (widget == null)
		{
			return;
		}

		widget.setHidden(hide);
	}

	private Color getColor(int timeLeft)
	{
		if (timeLeft < config.secondWarningTime())
		{
			return Color.RED;
		}
		else if (timeLeft < config.firstWarningTime())
		{
			return Color.YELLOW;
		}

		return Color.WHITE;
	}
}
